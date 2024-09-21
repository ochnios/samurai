package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.commons.AppConstants;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.security.AuthService;
import pl.ochnios.ninjabe.services.ConversationService;

import java.util.UUID;

import javax.json.JsonPatch;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

    private final AuthService authService;
    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<PageDto<ConversationSummaryDto>> getConversationsSummaries(
            PageRequestDto pageRequestDto) {
        final var user = authService.getAuthenticatedUser();
        final var summaries = conversationService.getSummariesPage(user, pageRequestDto);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(@PathVariable UUID conversationId) {
        final var user = authService.getAuthenticatedUser();
        final var conversation = conversationService.getConversation(user, conversationId);
        return ResponseEntity.ok(conversation);
    }

    @PatchMapping(
            value = "/{conversationId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, AppConstants.PATCH_MEDIA_TYPE})
    public ResponseEntity<ConversationDto> patchConversation(
            @PathVariable UUID conversationId, @RequestBody JsonPatch jsonPatch) {
        final var user = authService.getAuthenticatedUser();
        final var patchedConversation =
                conversationService.patchConversation(user, conversationId, jsonPatch);
        return ResponseEntity.ok(patchedConversation);
    }
}
