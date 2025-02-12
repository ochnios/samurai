package pl.ochnios.samurai.repositories.impl;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

public interface DocumentCrudRepository
        extends CrudRepository<DocumentEntity, UUID>, JpaSpecificationExecutor<DocumentEntity> {

    DocumentEntity findFirstByStatusOrderByCreatedAtAsc(DocumentStatus status);
}
