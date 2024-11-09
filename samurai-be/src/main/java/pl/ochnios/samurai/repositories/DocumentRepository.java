package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

import java.util.UUID;

public interface DocumentRepository {

    DocumentEntity findById(UUID documentId);

    Page<DocumentEntity> findAll(Specification<DocumentEntity> specification, Pageable pageable);

    DocumentEntity findFirstByStatus(DocumentStatus status);

    DocumentEntity save(DocumentEntity documentEntity);

    DocumentEntity saveAndFlush(DocumentEntity documentEntity);

    void delete(DocumentEntity documentEntity);
}
