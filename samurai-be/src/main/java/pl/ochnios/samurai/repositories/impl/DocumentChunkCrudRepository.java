package pl.ochnios.samurai.repositories.impl;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

public interface DocumentChunkCrudRepository
        extends CrudRepository<DocumentChunk, UUID>, JpaSpecificationExecutor<DocumentChunk> {

    Optional<DocumentChunk> findByDocumentIdAndId(UUID documentId, UUID chunkId);
}
