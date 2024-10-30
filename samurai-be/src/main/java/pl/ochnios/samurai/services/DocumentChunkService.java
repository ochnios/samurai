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
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunkSpecification;
import pl.ochnios.samurai.model.mappers.DocumentChunkMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunkService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final JsonPatchService patchService;
    private final EmbeddingService embeddingService;
    private final PageMapper pageMapper;
    private final DocumentChunkMapper chunkMapper;

    @Transactional(readOnly = true)
    public PageDto<DocumentChunkDto> getChunksPage(
            UUID documentId, DocumentChunkCriteria criteria, PageRequestDto pageRequestDto) {
        final var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        final var specification = DocumentChunkSpecification.create(criteria);
        final var documentChunksPage = chunkRepository.findAll(documentId, specification, pageRequest);
        return pageMapper.validOrDefaultSort(documentChunksPage, chunkMapper::map);
    }

    @Transactional
    public DocumentChunkDto saveChunk(UUID documentId, DocumentChunkDto chunkDto) {
        final var document = documentRepository.findById(documentId);
        final var chunk = chunkMapper.map(chunkDto, document);
        final var savedChunk = doAdd(documentId, chunk);
        final var embeddingChunk = chunkMapper.mapToEmbeddingChunk(savedChunk);
        embeddingService.add(embeddingChunk);
        return chunkMapper.map(savedChunk);
    }

    @Transactional
    public DocumentChunkDto patchChunk(UUID documentId, UUID chunkId, JsonPatch jsonPatch) {
        final var patched = chunkRepository.findById(documentId, chunkId);
        final var beforePatch = chunkMapper.map(patched);
        patchService.apply(patched, jsonPatch);

        DocumentChunk saved;
        if (beforePatch.getPosition() != patched.getPosition()) {
            saved = doMove(documentId, beforePatch.getPosition(), patched.getPosition());
        } else {
            saved = patched;
        }

        if (beforePatch.getContent().equals(patched.getContent())) {
            saved.setContent(patched.getContent());
            saved = chunkRepository.save(saved);
            embeddingService.update(chunkMapper.mapToEmbeddingChunk(saved));
        }

        log.info("Chunk {} patched", documentId);
        return chunkMapper.map(saved);
    }

    @Transactional
    public void deleteChunk(UUID documentId, UUID chunkId) {
        final var chunk = chunkRepository.findById(documentId, chunkId);
        chunkRepository.delete(chunk);
        reorderChunks(documentId, chunk.getPosition());
        embeddingService.delete(chunkMapper.mapToEmbeddingChunk(chunk));
        log.info("Chunk {} deleted", documentId);
    }

    private DocumentChunk doAdd(UUID documentId, DocumentChunk chunk) {
        final var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        validateNewPosition(chunks, chunk.getPosition());
        if (chunk.getPosition() == chunks.size()) {
            return chunkRepository.save(chunk);
        } else {
            chunks.add(chunk.getPosition(), chunk);
            final var reordered = reorderChunks(chunks, chunk.getPosition(), chunks.size());
            return reordered.get(chunk.getPosition());
        }
    }

    private void validateNewPosition(List<DocumentChunk> chunks, int newPosition) {
        if (newPosition < 0 || newPosition >= chunks.size()) {
            throw new ValidationException("Requested position is out of chunks range <0;" + chunks.size() + ">");
        }
    }

    private DocumentChunk doMove(UUID documentId, int fromPosition, int toPosition) {
        final var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        validateToPosition(chunks, toPosition);

        final var moved = chunks.remove(fromPosition);
        if (fromPosition < toPosition) {
            toPosition--;
        }
        chunks.add(toPosition, moved);

        int start = Math.min(fromPosition, toPosition);
        int end = Math.max(fromPosition, toPosition);
        final var reordered = reorderChunks(chunks, start, end);
        return reordered.get(toPosition);
    }

    private void validateToPosition(List<DocumentChunk> chunks, int toPosition) {
        if (toPosition < 0 || toPosition >= chunks.size()) {
            throw new ValidationException("Requested position is out of chunks range <0;" + chunks.size() + ")");
        }
    }

    private void reorderChunks(UUID documentId, int fromPosition) {
        final var chunks = chunkRepository.findAllByDocumentIdOrdered(documentId);
        reorderChunks(chunks, fromPosition, chunks.size());
    }

    private List<DocumentChunk> reorderChunks(List<DocumentChunk> chunks, int fromPosition, int toPosition) {
        for (int i = fromPosition; i < toPosition; i++) {
            if (chunks.get(i).getPosition() != i) {
                chunks.get(i).setPosition(i);
            }
        }
        final var savedChunks = chunkRepository.saveAll(chunks);
        return StreamSupport.stream(savedChunks.spliterator(), false).toList();
    }
}
