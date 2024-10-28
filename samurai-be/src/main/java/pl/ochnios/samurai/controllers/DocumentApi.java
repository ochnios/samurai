package pl.ochnios.samurai.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;
import pl.ochnios.samurai.model.dtos.document.DocumentDto;
import pl.ochnios.samurai.model.dtos.document.DocumentUploadDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;

import javax.json.JsonPatch;
import java.util.UUID;

import static pl.ochnios.samurai.commons.AppConstants.HTTP_200;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_400;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_404;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "${docs.ctrl.document.tag.name}", description = "${docs.ctrl.document.tag.desc}")
public interface DocumentApi {

    @Operation(summary = "${docs.ctrl.document.getDocuments}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<PageDto<DocumentDto>> getDocuments(
            @ParameterObject DocumentCriteria criteria, @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "${docs.ctrl.document.upload}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<DocumentDto> uploadDocument(@ModelAttribute DocumentUploadDto documentUploadDto);

    @Operation(summary = "${docs.ctrl.document.get}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<DocumentDto> getDocument(@PathVariable UUID documentId);

    @Operation(summary = "${docs.ctrl.document.download}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<byte[]> downloadDocument(@PathVariable UUID documentId);

    @Operation(summary = "${docs.ctrl.document.patch}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<DocumentDto> patchDocument(@PathVariable UUID documentId, @RequestBody JsonPatch jsonPatch);

    @Operation(summary = "${docs.ctrl.document.delete}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId);
}
