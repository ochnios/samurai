package pl.ochnios.samurai.repositories.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;

@Repository
@RequiredArgsConstructor
public class DocumentChunkRepositoryImpl implements DocumentChunkRepository {

    private final DocumentChunkCrudRepository documentChunkCrudRepository;

    @Override
    public DocumentChunk findById(UUID documentId, UUID chunkId) {
        return documentChunkCrudRepository
                .findByDocumentIdAndId(documentId, chunkId)
                .orElseThrow(() -> ResourceNotFoundException.of(DocumentChunk.class, chunkId));
    }

    @Override
    public Page<DocumentChunk> findAll(UUID documentId, Specification<DocumentChunk> specification, Pageable pageable) {
        final var fullSpecification = addDocumentIdToSpecification(documentId, specification);
        return documentChunkCrudRepository.findAll(fullSpecification, pageable);
    }

    @Override
    public DocumentChunk save(DocumentChunk documentChunk) {
        return documentChunkCrudRepository.save(documentChunk);
    }

    @Override
    public void delete(DocumentChunk documentChunk) {
        documentChunkCrudRepository.delete(documentChunk);
    }

    private Specification<DocumentChunk> addDocumentIdToSpecification(
            UUID documentId, Specification<DocumentChunk> specification) {
        return specification.and(Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("document").get("id"), documentId)));
    }
}
