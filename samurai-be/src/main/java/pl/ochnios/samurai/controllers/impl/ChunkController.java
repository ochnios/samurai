package pl.ochnios.samurai.controllers.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.controllers.ChunkApi;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.services.ChunkService;

import javax.json.JsonPatch;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/{documentId}/chunks")
public class ChunkController implements ChunkApi {

    private final ChunkService chunkService;

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping
    public ResponseEntity<PageDto<ChunkDto>> getChunks(
            @PathVariable UUID documentId,
            @ParameterObject ChunkCriteria criteria,
            @ParameterObject PageRequestDto pageRequestDto) {
        final var chunksPage = chunkService.getChunksPage(documentId, criteria, pageRequestDto);
        return ResponseEntity.ok(chunksPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PostMapping
    public ResponseEntity<ChunkDto> addChunk(
            @PathVariable UUID documentId, @Valid @RequestBody ChunkDto chunkDto) {
        final var savedChunk = chunkService.saveChunk(documentId, chunkDto);
        return ResponseEntity.ok(savedChunk);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @PatchMapping(value = "/{chunkId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<ChunkDto> patchChunk(
            @PathVariable UUID documentId, @PathVariable UUID chunkId, @RequestBody JsonPatch jsonPatch) {
        final var patchedChunk = chunkService.patchChunk(documentId, chunkId, jsonPatch);
        return ResponseEntity.ok(patchedChunk);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @DeleteMapping(value = "/{chunkId}")
    public ResponseEntity<Void> deleteChunk(@PathVariable UUID documentId, @PathVariable UUID chunkId) {
        chunkService.deleteChunk(documentId, chunkId);
        return ResponseEntity.noContent().build();
    }
}
