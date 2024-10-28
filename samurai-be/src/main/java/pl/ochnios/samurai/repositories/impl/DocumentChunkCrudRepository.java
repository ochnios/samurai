package pl.ochnios.samurai.repositories.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;

import java.util.Optional;
import java.util.UUID;

public interface DocumentChunkCrudRepository
        extends CrudRepository<DocumentChunk, UUID>, JpaSpecificationExecutor<DocumentChunk> {

    Optional<DocumentChunk> findByDocumentIdAndId(UUID documentId, UUID chunkId);

    Page<DocumentChunk> findAllByDocumentId(
            UUID documentId, Specification<DocumentChunk> specification, Pageable pageable);
}
