package pl.ochnios.samurai.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.samurai.commons.exceptions.ValidationException;
import pl.ochnios.samurai.commons.patch.JsonPatchService;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.model.entities.document.chunk.ChunkSpecification;
import pl.ochnios.samurai.model.mappers.ChunkMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.ChunkRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final JsonPatchService patchService;
    private final EmbeddingService embeddingService;
    private final PageMapper pageMapper;
    private final ChunkMapper chunkMapper;

    @Transactional(readOnly = true)
    public PageDto<ChunkDto> getChunksPage(UUID documentId, ChunkCriteria criteria, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        var specification = ChunkSpecification.create(criteria);
        var chunksPage = chunkRepository.findAll(documentId, specification, pageRequest);
        return pageMapper.validOrDefaultSort(chunksPage, chunkMapper::map);
    }

    @Transactional
    public ChunkDto saveChunk(UUID documentId, ChunkDto chunkDto) {
        var document = documentRepository.findById(documentId);
        var chunk = chunkMapper.map(chunkDto, document);
        var savedChunk = doAdd(documentId, chunk);
        var embeddingChunk = chunkMapper.mapToEmbeddedChunk(savedChunk);
        embeddingService.add(embeddingChunk);
        return chunkMapper.map(savedChunk);
    }

    @Transactional
    public ChunkDto patchChunk(UUID documentId, UUID chunkId, JsonPatch jsonPatch) {
        var chunk = chunkRepository.findById(documentId, chunkId);
        var patched = chunkMapper.copy(chunk);
        patchService.apply(patched, jsonPatch);

        Chunk saved;
        if (chunk.getPosition() != patched.getPosition()) {
            saved = doMove(documentId, chunk.getPosition(), patched.getPosition());
        } else {
            saved = chunk;
        }

        if (!chunk.getContent().equals(patched.getContent())) {
            saved.setContent(patched.getContent());
            saved = chunkRepository.save(saved);
            embeddingService.update(chunkMapper.mapToEmbeddedChunk(saved));
        }

        log.info("Chunk {} patched", documentId);
        return chunkMapper.map(saved);
    }

    @Transactional
    public void deleteChunk(UUID documentId, UUID chunkId) {
        var chunk = chunkRepository.findById(documentId, chunkId);
        chunkRepository.delete(chunk);
        reorderChunks(documentId, chunk.getPosition());
        embeddingService.delete(chunkMapper.mapToEmbeddedChunk(chunk));
        log.info("Chunk {} deleted", chunkId);
    }

    private Chunk doAdd(UUID documentId, Chunk chunk) {
        var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        validateNewPosition(chunks, chunk.getPosition());
        if (chunk.getPosition() == chunks.size()) {
            return chunkRepository.save(chunk);
        } else {
            chunks.add(chunk.getPosition(), chunk);
            var reordered = reorderChunks(chunks, chunk.getPosition(), chunks.size());
            return reordered.get(chunk.getPosition());
        }
    }

    private void validateNewPosition(List<Chunk> chunks, int newPosition) {
        if (newPosition < 0 || newPosition > chunks.size()) {
            throw new ValidationException("Requested position is out of chunks range <0;" + chunks.size() + ">");
        }
    }

    private Chunk doMove(UUID documentId, int fromPosition, int toPosition) {
        var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        validateToPosition(chunks, toPosition);

        var moved = chunks.remove(fromPosition);
        chunks.add(toPosition, moved);

        int start = Math.min(fromPosition, toPosition);
        int end = Math.max(fromPosition + 1, toPosition + 1);
        var reordered = reorderChunks(chunks, start, end);
        return reordered.get(toPosition);
    }

    private void validateToPosition(List<Chunk> chunks, int toPosition) {
        if (toPosition < 0 || toPosition >= chunks.size()) {
            throw new ValidationException("Requested position is out of chunks range <0;" + chunks.size() + ")");
        }
    }

    private void reorderChunks(UUID documentId, int fromPosition) {
        var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        reorderChunks(chunks, fromPosition, chunks.size());
    }

    private List<Chunk> reorderChunks(List<Chunk> chunks, int start, int end) {
        for (int i = start; i < end; i++) {
            if (chunks.get(i).getPosition() != i) {
                chunks.get(i).setPosition(i);
            }
        }
        var savedChunks = chunkRepository.saveAll(chunks);
        return StreamSupport.stream(savedChunks.spliterator(), false).toList();
    }
}
