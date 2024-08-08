package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.services.ConversationService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/assistants/{assistantId}/conversations")
    public List<ConversationSummaryDto> getConversationsSummaries(
            @PathVariable UUID assistantId, @RequestParam(required = false) Integer limit) {
        return conversationService.getConversationsSummaries(null, assistantId, limit);
    }

    @GetMapping("/assistants/{assistantId}/conversations/{conversationId}")
    public ConversationDto getConversation(
            @PathVariable UUID assistantId, @PathVariable UUID conversationId) {
        return conversationService.getConversation(null, assistantId, conversationId);
    }
}
