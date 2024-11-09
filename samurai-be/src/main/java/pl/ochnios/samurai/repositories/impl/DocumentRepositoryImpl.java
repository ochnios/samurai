package pl.ochnios.samurai.repositories.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository documentJpaRepository;

    @Override
    public DocumentEntity findById(UUID documentId) {
        return documentJpaRepository
                .findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of(DocumentEntity.class, documentId));
    }

    @Override
    public Page<DocumentEntity> findAll(Specification<DocumentEntity> specification, Pageable pageable) {
        return documentJpaRepository.findAll(specification, pageable);
    }

    @Override
    public DocumentEntity findFirstByStatus(DocumentStatus status) {
        return documentJpaRepository.findFirstByStatusOrderByCreatedAtAsc(status);
    }

    @Override
    public DocumentEntity save(DocumentEntity documentEntity) {
        return documentJpaRepository.save(documentEntity);
    }

    @Override
    public DocumentEntity saveAndFlush(DocumentEntity documentEntity) {
        return documentJpaRepository.saveAndFlush(documentEntity);
    }

    @Override
    public void delete(DocumentEntity documentEntity) {
        documentJpaRepository.delete(documentEntity);
    }
}
