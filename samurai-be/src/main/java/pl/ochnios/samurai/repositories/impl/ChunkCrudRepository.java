package pl.ochnios.samurai.repositories.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;

public interface ChunkCrudRepository extends CrudRepository<Chunk, UUID>, JpaSpecificationExecutor<Chunk> {

    List<Chunk> findAllByDocumentIdOrderByPositionAsc(UUID documentId);

    Optional<Chunk> findByDocumentIdAndId(UUID documentId, UUID chunkId);
}
