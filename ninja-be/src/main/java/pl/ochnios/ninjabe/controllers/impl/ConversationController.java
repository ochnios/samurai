package pl.ochnios.ninjabe.controllers.impl;

import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.ninjabe.commons.AppConstants;
import pl.ochnios.ninjabe.controllers.ConversationApi;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationCriteria;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.services.ConversationService;
import pl.ochnios.ninjabe.services.security.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController implements ConversationApi {

    private final AuthService authService;
    private final ConversationService conversationService;

    @Override
    @GetMapping("/summaries")
    public ResponseEntity<PageDto<ConversationSummaryDto>> getSummaries(PageRequestDto pageRequestDto) {
        final var user = authService.getAuthenticatedUser();
        final var summariesPage = conversationService.getSummariesPage(user, pageRequestDto);
        return ResponseEntity.ok(summariesPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping
    public ResponseEntity<PageDto<ConversationDetailsDto>> getConversations(
            ConversationCriteria conversationCriteria, PageRequestDto pageRequestDto) {
        final var detailsPage = conversationService.getDetailsPage(conversationCriteria, pageRequestDto);
        return ResponseEntity.ok(detailsPage);
    }

    @Override
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(@PathVariable UUID conversationId) {
        final var user = authService.getAuthenticatedUser();
        final var conversation = conversationService.getConversation(user, conversationId);
        return ResponseEntity.ok(conversation);
    }

    @Override
    @PatchMapping(value = "/{conversationId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<ConversationDto> patchConversation(
            @PathVariable UUID conversationId, @RequestBody JsonPatch jsonPatch) {
        final var user = authService.getAuthenticatedUser();
        final var patchedConversation = conversationService.patchConversation(user, conversationId, jsonPatch);
        return ResponseEntity.ok(patchedConversation);
    }

    @Override
    @DeleteMapping(value = "/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId) {
        final var user = authService.getAuthenticatedUser();
        conversationService.deleteConversation(user, conversationId);
        return ResponseEntity.noContent().build();
    }
}
