package pl.ochnios.samurai.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;

import javax.json.JsonPatch;
import java.util.UUID;

import static pl.ochnios.samurai.commons.AppConstants.HTTP_200;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_400;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_404;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "${docs.ctrl.document.chunk.tag.name}", description = "${docs.ctrl.document.chunk.tag.desc}")
public interface ChunkApi {

    @Operation(summary = "${docs.ctrl.document.chunk.searchChunks}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<PageDto<ChunkDto>> getChunks(
            @PathVariable UUID documentId,
            @ParameterObject ChunkCriteria criteria,
            @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "${docs.ctrl.document.chunk.add}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<ChunkDto> addChunk(
            @PathVariable UUID documentId, @RequestBody ChunkDto chunkDto);

    @Operation(summary = "${docs.ctrl.document.chunk.patch}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<ChunkDto> patchChunk(
            @PathVariable UUID documentId, @PathVariable UUID chunkId, @RequestBody JsonPatch jsonPatch);

    @Operation(summary = "${docs.ctrl.document.chunk.delete}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<Void> deleteChunk(@PathVariable UUID documentId, @PathVariable UUID chunkId);
}
