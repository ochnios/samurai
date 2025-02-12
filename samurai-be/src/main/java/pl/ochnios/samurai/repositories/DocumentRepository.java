package pl.ochnios.samurai.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

public interface DocumentRepository {

    DocumentEntity findById(UUID documentId);

    Page<DocumentEntity> findAll(Specification<DocumentEntity> specification, Pageable pageable);

    List<DocumentEntity> findAllById(Iterable<UUID> ids);

    DocumentEntity findFirstByStatus(DocumentStatus status);

    DocumentEntity save(DocumentEntity documentEntity);

    void delete(DocumentEntity documentEntity);
}
