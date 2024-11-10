package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;

import java.util.List;
import java.util.UUID;

public interface ChunkRepository {

    Chunk findById(UUID documentId, UUID chunkId);

    List<Chunk> findAllByDocumentIdOrdered(UUID documentId);

    Page<Chunk> findAll(UUID documentId, Specification<Chunk> specification, Pageable pageable);

    Chunk save(Chunk chunk);

    List<Chunk> saveAll(List<Chunk> chunks);

    void delete(Chunk chunk);

    void deleteAll(List<Chunk> chunks);
}
