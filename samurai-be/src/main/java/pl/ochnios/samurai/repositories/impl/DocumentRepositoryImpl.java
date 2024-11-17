package pl.ochnios.samurai.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;
import pl.ochnios.samurai.repositories.DocumentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentCrudRepository documentCrudRepository;

    @Override
    public DocumentEntity findById(UUID documentId) {
        return documentCrudRepository
                .findById(documentId)
                .orElseThrow(() -> ResourceNotFoundException.of(DocumentEntity.class, documentId));
    }

    @Override
    public Page<DocumentEntity> findAll(Specification<DocumentEntity> specification, Pageable pageable) {
        return documentCrudRepository.findAll(specification, pageable);
    }

    @Override
    public List<DocumentEntity> findAllById(Iterable<UUID> ids) {
        return StreamSupport.stream(documentCrudRepository.findAllById(ids).spliterator(), false)
                .toList();
    }

    @Override
    public DocumentEntity findFirstByStatus(DocumentStatus status) {
        return documentCrudRepository.findFirstByStatusOrderByCreatedAtAsc(status);
    }

    @Override
    public DocumentEntity save(DocumentEntity documentEntity) {
        return documentCrudRepository.save(documentEntity);
    }

    @Override
    public void delete(DocumentEntity documentEntity) {
        documentCrudRepository.delete(documentEntity);
    }
}
