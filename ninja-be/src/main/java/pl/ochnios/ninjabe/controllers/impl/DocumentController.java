package pl.ochnios.ninjabe.controllers.impl;

import jakarta.validation.Valid;
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
import pl.ochnios.ninjabe.commons.AppConstants;
import pl.ochnios.ninjabe.controllers.DocumentApi;
import pl.ochnios.ninjabe.model.dtos.document.DocumentCriteria;
import pl.ochnios.ninjabe.model.dtos.document.DocumentDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentUploadDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.services.DocumentService;
import pl.ochnios.ninjabe.services.security.AuthService;

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
        final var documentsPage = documentService.getDocumentsPage(documentCriteria, pageRequestDto);
        return ResponseEntity.ok(documentsPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> uploadDocument(@ModelAttribute @Valid DocumentUploadDto documentUploadDto) {
        final var user = authService.getAuthenticatedUser();
        final var savedDocument = documentService.saveDocument(user, documentUploadDto);
        return ResponseEntity.ok(savedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable UUID documentId) {
        final var document = documentService.getDocument(documentId);
        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasRole('MOD')")
    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID documentId) {
        final var documentFile = documentService.getDocumentFile(documentId);
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", documentFile.getName());
        return new ResponseEntity<>(documentFile.getContent(), headers, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PatchMapping(value = "/{documentId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<DocumentDto> patchDocument(@PathVariable UUID documentId, @RequestBody JsonPatch jsonPatch) {
        final var patchedDocument = documentService.patchDocument(documentId, jsonPatch);
        return ResponseEntity.ok(patchedDocument);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @DeleteMapping(value = "/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
