package pl.ochnios.ninjabe.services;

import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.ninjabe.commons.patch.JsonPatchService;
import pl.ochnios.ninjabe.model.dtos.document.DocumentCriteria;
import pl.ochnios.ninjabe.model.dtos.document.DocumentDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentUploadDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.model.entities.document.DocumentSpecification;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.DocumentMapper;
import pl.ochnios.ninjabe.model.mappers.PageMapper;
import pl.ochnios.ninjabe.repositories.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public DocumentDto getDocument(UUID documentId) {
        final var document = documentRepository.findById(documentId);
        return documentMapper.map(document);
    }

    @Transactional(readOnly = true)
    public PageDto<DocumentDto> getDocumentsPage(DocumentCriteria criteria, PageRequestDto pageRequestDto) {
        final var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        final var specification = DocumentSpecification.create(criteria);
        final var documentsPage = documentRepository.findAll(specification, pageRequest);
        return pageMapper.validOrDefaultSort(documentsPage, documentMapper::map);
    }

    @Transactional
    public DocumentDto saveDocument(User user, DocumentUploadDto documentUploadDto) {
        final var document = documentMapper.map(user, documentUploadDto);
        final var savedDocument = documentRepository.save(document);
        log.info("Document {} saved", savedDocument.getId());
        return documentMapper.map(savedDocument);
    }

    @Transactional
    public DocumentDto patchDocument(UUID documentId, JsonPatch jsonPatch) {
        final var document = documentRepository.findById(documentId);
        patchService.apply(document, jsonPatch);
        final var savedDocument = documentRepository.save(document);
        log.info("Document {} patched", documentId);
        return documentMapper.map(savedDocument);
    }

    @Transactional
    public void deleteDocument(UUID documentId) {
        final var document = documentRepository.findById(documentId);
        documentRepository.delete(document);
        log.info("Document {} deleted", documentId);
    }
}
