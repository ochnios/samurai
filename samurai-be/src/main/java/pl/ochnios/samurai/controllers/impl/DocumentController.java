package pl.ochnios.samurai.controllers.impl;

import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.controllers.DocumentApi;
import pl.ochnios.samurai.model.dtos.document.DocumentContentDto;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.dtos.document.DocumentUploadDto;
import pl.ochnios.samurai.model.dtos.file.FileDownloadDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.services.DocumentService;
import pl.ochnios.samurai.services.security.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController implements DocumentApi {

    private final AuthService authService;
    private final DocumentService documentService;

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping
    public ResponseEntity<PageDto<DocumentDto>> getDocuments(
            DocumentCriteria documentCriteria, PageRequestDto pageRequestDto) {
        var documentsPage = documentService.getPage(documentCriteria, pageRequestDto);
        return ResponseEntity.ok(documentsPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> uploadDocument(@ModelAttribute @Valid DocumentUploadDto documentUploadDto) {
        var user = authService.getAuthenticatedUser();
        var savedDocument = documentService.save(user, documentUploadDto);
        return ResponseEntity.ok(savedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable UUID documentId) {
        var document = documentService.get(documentId);
        return ResponseEntity.ok(document);
    }

    @Override
    @GetMapping(value = "/{documentId}/content")
    public ResponseEntity<DocumentContentDto> getDocumentContent(@PathVariable UUID documentId) {
        String content = documentService.getContentById(documentId);
        return ResponseEntity.ok(new DocumentContentDto(documentId, content));
    }

    @GetMapping(value = "/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable UUID documentId, @RequestParam(required = false) Boolean inline) {
        var documentFile = documentService.getFile(documentId);
        var headers = createFileDownloadHeaders(documentFile, inline);
        return new ResponseEntity<>(documentFile.getContent(), headers, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PatchMapping(value = "/{documentId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<DocumentDto> patchDocument(@PathVariable UUID documentId, @RequestBody JsonPatch jsonPatch) {
        var patchedDocument = documentService.patch(documentId, jsonPatch);
        return ResponseEntity.ok(patchedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @DeleteMapping(value = "/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId) {
        documentService.delete(documentId);
        return ResponseEntity.noContent().build();
    }

    private HttpHeaders createFileDownloadHeaders(FileDownloadDto file, Boolean inline) {
        return inline != null && inline
                ? createFileDownloadHeaders(file, "inline; filename*=")
                : createFileDownloadHeaders(file, "form-data; name=\"attachment\"; filename*=");
    }

    private HttpHeaders createFileDownloadHeaders(FileDownloadDto file, String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        String encodedFilename = "UTF-8''"
                + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue + encodedFilename);
        return headers;
    }
}
