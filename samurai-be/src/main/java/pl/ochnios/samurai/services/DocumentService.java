package pl.ochnios.samurai.services;

import static pl.ochnios.samurai.model.entities.document.DocumentStatus.ACTIVE;

import java.util.List;
import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.samurai.commons.exceptions.ValidationException;
import pl.ochnios.samurai.commons.patch.JsonPatchService;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.dtos.document.DocumentUploadDto;
import pl.ochnios.samurai.model.dtos.file.FileDownloadDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.document.DocumentSpecification;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.DocumentMapper;
import pl.ochnios.samurai.model.mappers.FileMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final FileMapper fileMapper;
    private final DocumentMapper documentMapper;

    @Value("${spring.servlet.multipart.max-file-size:50MB}")
    private DataSize maxFileSize;

    @Transactional(readOnly = true)
    public DocumentDto get(UUID documentId) {
        var document = documentRepository.findById(documentId);
        return documentMapper.map(document);
    }

    @Transactional(readOnly = true)
    public FileDownloadDto getFile(UUID documentId) {
        var document = documentRepository.findById(documentId);
        return fileMapper.mapToDownloadDto(document);
    }

    @Transactional(readOnly = true)
    public PageDto<DocumentDto> getPage(DocumentCriteria criteria, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.map(pageRequestDto);
        var specification = DocumentSpecification.create(criteria);
        var documentsPage = documentRepository.findAll(specification, pageRequest);
        return pageMapper.map(documentsPage, documentMapper::map);
    }

    @Transactional
    public DocumentDto save(User user, DocumentUploadDto documentUploadDto) {
        // TODO auto generating document summary if requested
        validateFileSize(documentUploadDto.getFile());
        var document = documentMapper.map(user, documentUploadDto);
        var savedDocument = documentRepository.save(document);
        log.info("Document {} saved", savedDocument.getId());
        return documentMapper.map(savedDocument);
    }

    @Transactional
    public DocumentDto patch(UUID documentId, JsonPatch jsonPatch) {
        var document = documentRepository.findById(documentId);
        patchService.apply(document, jsonPatch);
        var savedDocument = documentRepository.save(document);
        log.info("Document {} patched", documentId);
        return documentMapper.map(savedDocument);
    }

    @Transactional
    public void delete(UUID documentId) {
        var document = documentRepository.findById(documentId);
        documentRepository.delete(document);
        log.info("Document {} deleted", documentId);
    }

    @Transactional
    public List<DocumentDto> getActiveDocuments() {
        var criteria = DocumentCriteria.builder().status(ACTIVE).build();
        var specification = DocumentSpecification.create(criteria);
        var documents = documentRepository.findAll(specification, Pageable.unpaged());
        return pageMapper.map(documents, documentMapper::map).getItems();
    }

    private void validateFileSize(MultipartFile multipartFile) {
        if (multipartFile.getSize() > maxFileSize.toBytes()) {
            throw new ValidationException("File size must not be greater than " + maxFileSize);
        }
    }
}
