package pl.ochnios.samurai.repositories.impl;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChunkCrudRepository
        extends CrudRepository<Chunk, UUID>, JpaSpecificationExecutor<Chunk> {

    List<Chunk> findAllByDocumentIdOrderByPositionAsc(UUID documentId);

    Optional<Chunk> findByDocumentIdAndId(UUID documentId, UUID chunkId);
}
