package pl.ochnios.ninjabe.repositories.impl;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;

public interface DocumentCrudRepository
        extends CrudRepository<DocumentEntity, UUID>, JpaSpecificationExecutor<DocumentEntity> {}
