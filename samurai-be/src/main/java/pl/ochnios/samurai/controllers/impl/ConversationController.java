package pl.ochnios.samurai.controllers.impl;

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
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.controllers.ConversationApi;
import pl.ochnios.samurai.model.dtos.conversation.ConversationCriteria;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.services.ConversationService;
import pl.ochnios.samurai.services.security.AuthService;

import javax.json.JsonPatch;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController implements ConversationApi {

    private final AuthService authService;
    private final ConversationService conversationService;

    @Override
    @GetMapping("/summaries")
    public ResponseEntity<PageDto<ConversationSummaryDto>> getSummaries(PageRequestDto pageRequestDto) {
        var user = authService.getAuthenticatedUser();
        var summariesPage = conversationService.getSummariesPage(user, pageRequestDto);
        return ResponseEntity.ok(summariesPage);
    }

    @Override
    @PreAuthorize("hasRole('MOD')")
    @GetMapping
    public ResponseEntity<PageDto<ConversationDetailsDto>> searchConversations(
            ConversationCriteria conversationCriteria, PageRequestDto pageRequestDto) {
        var detailsPage = conversationService.getDetailsPage(conversationCriteria, pageRequestDto);
        return ResponseEntity.ok(detailsPage);
    }

    @Override
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(@PathVariable UUID conversationId) {
        var user = authService.getAuthenticatedUser();
        var conversation = conversationService.getConversationPreview(user, conversationId);
        return ResponseEntity.ok(conversation);
    }

    @Override
    @PatchMapping(value = "/{conversationId}", consumes = AppConstants.PATCH_MEDIA_TYPE)
    public ResponseEntity<ConversationDto> patchConversation(
            @PathVariable UUID conversationId, @RequestBody JsonPatch jsonPatch) {
        var user = authService.getAuthenticatedUser();
        var patchedConversation = conversationService.patchConversation(user, conversationId, jsonPatch);
        return ResponseEntity.ok(patchedConversation);
    }

    @Override
    @DeleteMapping(value = "/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId) {
        var user = authService.getAuthenticatedUser();
        conversationService.deleteConversation(user, conversationId);
        return ResponseEntity.noContent().build();
    }
}
