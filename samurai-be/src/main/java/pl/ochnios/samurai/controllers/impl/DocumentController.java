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
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.controllers.DocumentApi;
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
        var documentsPage = documentService.getDocumentsPage(documentCriteria, pageRequestDto);
        return ResponseEntity.ok(documentsPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> uploadDocument(@ModelAttribute @Valid DocumentUploadDto documentUploadDto) {
        var user = authService.getAuthenticatedUser();
        var savedDocument = documentService.saveDocument(user, documentUploadDto);
        return ResponseEntity.ok(savedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable UUID documentId) {
        var document = documentService.getDocument(documentId);
        return ResponseEntity.ok(document);
    }

    @GetMapping(value = "/{documentId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID documentId) {
        var documentFile = documentService.getDocumentFile(documentId);
        var headers = createFileDownloadHeaders(documentFile);
        return new ResponseEntity<>(documentFile.getContent(), headers, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PatchMapping(value = "/{documentId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<DocumentDto> patchDocument(@PathVariable UUID documentId, @RequestBody JsonPatch jsonPatch) {
        var patchedDocument = documentService.patchDocument(documentId, jsonPatch);
        return ResponseEntity.ok(patchedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @DeleteMapping(value = "/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    private HttpHeaders createFileDownloadHeaders(FileDownloadDto file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String encodedFilename = "UTF-8''"
                + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename*=" + encodedFilename);
        return headers;
    }
}
