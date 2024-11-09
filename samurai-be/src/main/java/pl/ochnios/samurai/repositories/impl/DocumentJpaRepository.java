package pl.ochnios.samurai.repositories.impl;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

public interface DocumentJpaRepository
        extends JpaRepository<DocumentEntity, UUID>, JpaSpecificationExecutor<DocumentEntity> {

    DocumentEntity findFirstByStatusOrderByCreatedAtAsc(DocumentStatus status);
}
