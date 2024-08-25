package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.services.ConversationService;
import pl.ochnios.ninjabe.services.UserService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

    private final UserService userService;
    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationSummaryDto> getConversationsSummaries(
            @RequestParam(required = false) Integer limit) {
        final var user = userService.getCurrentUser();
        return conversationService.getSummaries(user, limit);
    }

    @GetMapping("/{conversationId}")
    public ConversationDto getConversation(@PathVariable UUID conversationId) {
        final var user = userService.getCurrentUser();
        return conversationService.getConversation(user, conversationId);
    }
}
