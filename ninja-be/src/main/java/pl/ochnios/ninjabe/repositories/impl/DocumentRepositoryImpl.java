package pl.ochnios.ninjabe.repositories.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.ninjabe.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;
import pl.ochnios.ninjabe.repositories.DocumentRepository;

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
    public DocumentEntity save(DocumentEntity documentEntity) {
        return documentCrudRepository.save(documentEntity);
    }

    @Override
    public void delete(DocumentEntity documentEntity) {
        documentCrudRepository.delete(documentEntity);
    }
}
