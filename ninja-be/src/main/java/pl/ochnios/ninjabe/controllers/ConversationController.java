package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.security.AuthService;
import pl.ochnios.ninjabe.services.ConversationService;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

    private final AuthService authService;
    private final ConversationService conversationService;

    @GetMapping
    public PageDto<ConversationSummaryDto> getConversationsSummaries(
            PageRequestDto pageRequestDto) {
        final var user = authService.getAuthenticatedUser();
        return conversationService.getSummariesPage(user, pageRequestDto);
    }

    @GetMapping("/{conversationId}")
    public ConversationDto getConversation(@PathVariable UUID conversationId) {
        final var user = authService.getAuthenticatedUser();
        return conversationService.getConversation(user, conversationId);
    }
}
