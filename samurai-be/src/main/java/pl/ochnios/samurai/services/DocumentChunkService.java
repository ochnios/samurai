package pl.ochnios.samurai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.commons.patch.JsonPatchService;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunkSpecification;
import pl.ochnios.samurai.model.mappers.DocumentChunkMapper;
import pl.ochnios.samurai.model.mappers.DocumentMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.DocumentChunkRepository;

import javax.json.JsonPatch;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunkService {

    private final DocumentChunkRepository documentChunkRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final DocumentChunkMapper documentChunkMapper;
    private final DocumentMapper documentMapper;

    @Transactional(readOnly = true)
    public PageDto<DocumentChunkDto> getDocumentChunksPage(
            UUID documentId, DocumentChunkCriteria criteria, PageRequestDto pageRequestDto) {
        final var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        final var specification = DocumentChunkSpecification.create(criteria);
        final var documentChunksPage = documentChunkRepository.findAll(documentId, specification, pageRequest);
        return pageMapper.validOrDefaultSort(documentChunksPage, documentChunkMapper::map);
    }

    @Transactional
    public DocumentChunkDto saveDocumentChunk(UUID documentId, DocumentChunkDto documentChunkDto) {
        throw new ApplicationException("Not implemented yet");
    }

    @Transactional
    public DocumentChunkDto patchDocumentChunk(UUID documentId, UUID chunkId, JsonPatch jsonPatch) {
        final var chunk = documentChunkRepository.findById(documentId, chunkId);
        patchService.apply(chunk, jsonPatch);
        final var savedChunk = documentChunkRepository.save(chunk);
        log.info("Chunk {} patched", documentId);
        return documentChunkMapper.map(savedChunk);
    }

    @Transactional
    public void deleteDocumentChunk(UUID documentId, UUID chunkId) {
        final var chunk = documentChunkRepository.findById(documentId, chunkId);
        documentChunkRepository.delete(chunk);
        log.info("Chunk {} deleted", documentId);
    }
}
