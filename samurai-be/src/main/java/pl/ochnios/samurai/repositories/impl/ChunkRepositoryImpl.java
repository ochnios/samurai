package pl.ochnios.samurai.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.repositories.ChunkRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChunkRepositoryImpl implements ChunkRepository {

    private final ChunkCrudRepository chunkCrudRepository;

    @Override
    public Chunk findById(UUID documentId, UUID chunkId) {
        return chunkCrudRepository
                .findByDocumentIdAndId(documentId, chunkId)
                .orElseThrow(() -> ResourceNotFoundException.of(Chunk.class, chunkId));
    }

    @Override
    public List<Chunk> findAllByDocumentIdOrdered(UUID documentId) {
        return chunkCrudRepository.findAllByDocumentIdOrderByPositionAsc(documentId);
    }

    @Override
    public Page<Chunk> findAll(UUID documentId, Specification<Chunk> specification, Pageable pageable) {
        final var fullSpecification = addDocumentIdToSpecification(documentId, specification);
        return chunkCrudRepository.findAll(fullSpecification, pageable);
    }

    @Override
    public Chunk save(Chunk chunk) {
        return chunkCrudRepository.save(chunk);
    }

    @Override
    public Iterable<Chunk> saveAll(List<Chunk> chunks) {
        return chunkCrudRepository.saveAll(chunks);
    }

    @Override
    public void delete(Chunk chunk) {
        chunkCrudRepository.delete(chunk);
    }

    private Specification<Chunk> addDocumentIdToSpecification(
            UUID documentId, Specification<Chunk> specification) {
        return specification.and(Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("document").get("id"), documentId)));
    }
}
