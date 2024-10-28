package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;

import java.util.UUID;

public interface DocumentRepository {

    DocumentEntity findById(UUID documentId);

    Page<DocumentEntity> findAll(Specification<DocumentEntity> specification, Pageable pageable);

    DocumentEntity save(DocumentEntity documentEntity);

    void delete(DocumentEntity documentEntity);
}
