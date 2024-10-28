package pl.ochnios.samurai.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;

import java.util.UUID;

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
        return documentChunkCrudRepository.findAllByDocumentId(documentId, specification, pageable);
    }

    @Override
    public DocumentChunk save(DocumentChunk documentChunk) {
        return documentChunkCrudRepository.save(documentChunk);
    }

    @Override
    public void delete(DocumentChunk documentChunk) {
        documentChunkCrudRepository.delete(documentChunk);
    }
}
