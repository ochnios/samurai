package pl.ochnios.samurai.controllers.impl;

import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.controllers.DocumentChunkApi;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.services.DocumentChunkService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/{documentId}/chunks")
public class DocumentChunkController implements DocumentChunkApi {

    private final DocumentChunkService documentChunkService;

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping
    public ResponseEntity<PageDto<DocumentChunkDto>> getChunks(
            @PathVariable UUID documentId,
            @ParameterObject DocumentChunkCriteria criteria,
            @ParameterObject PageRequestDto pageRequestDto) {
        final var chunksPage = documentChunkService.getChunksPage(documentId, criteria, pageRequestDto);
        return ResponseEntity.ok(chunksPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PostMapping
    public ResponseEntity<DocumentChunkDto> addChunk(
            @PathVariable UUID documentId, @ModelAttribute DocumentChunkDto documentChunkDto) {
        final var savedChunk = documentChunkService.saveChunk(documentId, documentChunkDto);
        return ResponseEntity.ok(savedChunk);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PatchMapping(value = "/{chunkId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<DocumentChunkDto> patchChunk(
            @PathVariable UUID documentId, @PathVariable UUID chunkId, @RequestBody JsonPatch jsonPatch) {
        final var patchedChunk = documentChunkService.patchChunk(documentId, chunkId, jsonPatch);
        return ResponseEntity.ok(patchedChunk);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @DeleteMapping(value = "/{chunkId}")
    public ResponseEntity<Void> deleteChunk(@PathVariable UUID documentId, @PathVariable UUID chunkId) {
        documentChunkService.deleteChunk(documentId, chunkId);
        return ResponseEntity.noContent().build();
    }
}
