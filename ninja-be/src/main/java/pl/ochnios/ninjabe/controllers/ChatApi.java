package pl.ochnios.ninjabe.controllers;

import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_200;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_400;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_404;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "${docs.ctrl.chat.tag.name}", description = "${docs.ctrl.chat.tag.desc}")
public interface ChatApi {

    @Operation(summary = "${docs.ctrl.chat.chat}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto chatRequestDto);
}
