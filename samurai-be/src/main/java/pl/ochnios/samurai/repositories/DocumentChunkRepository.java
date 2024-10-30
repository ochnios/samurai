package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository {

    DocumentChunk findById(UUID documentId, UUID chunkId);

    List<DocumentChunk> findAllByDocumentIdOrdered(UUID documentId);

    Page<DocumentChunk> findAll(UUID documentId, Specification<DocumentChunk> specification, Pageable pageable);

    DocumentChunk save(DocumentChunk documentChunk);

    Iterable<DocumentChunk> saveAll(List<DocumentChunk> documentChunks);

    void delete(DocumentChunk documentChunk);
}
