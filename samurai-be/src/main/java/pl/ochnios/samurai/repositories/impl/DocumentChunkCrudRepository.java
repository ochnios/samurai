package pl.ochnios.samurai.repositories.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

public interface DocumentChunkCrudRepository
        extends CrudRepository<DocumentChunk, UUID>, JpaSpecificationExecutor<DocumentChunk> {

    List<DocumentChunk> findAllByDocumentIdOrderByPositionAsc(UUID documentId);

    Optional<DocumentChunk> findByDocumentIdAndId(UUID documentId, UUID chunkId);
}
