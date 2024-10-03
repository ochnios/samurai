package pl.ochnios.ninjabe.controllers;

import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_200;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_400;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_404;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import javax.json.JsonPatch;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "${docs.ctrl.conversation.tag.name}", description = "${docs.ctrl.conversation.tag.desc}")
public interface ConversationApi {

    @Operation(summary = "${docs.ctrl.conversation.getSummaries}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<PageDto<ConversationSummaryDto>> getSummaries(@ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "${docs.ctrl.conversation.get}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<ConversationDto> getConversation(@PathVariable UUID conversationId);

    @Operation(summary = "${docs.ctrl.conversation.patch}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<ConversationDto> patchConversation(
            @PathVariable UUID conversationId, @RequestBody JsonPatch jsonPatch);

    @Operation(summary = "${docs.ctrl.conversation.delete}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId);
}
