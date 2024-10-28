package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

import java.util.UUID;

public interface DocumentChunkRepository {

    DocumentChunk findById(UUID documentId, UUID chunkId);

    Page<DocumentChunk> findAll(UUID documentId, Specification<DocumentChunk> specification, Pageable pageable);

    DocumentChunk save(DocumentChunk documentChunk);

    void delete(DocumentChunk documentChunk);
}
