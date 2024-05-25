package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public List<ConversationSummaryDto> getConversationsSummaries(@PathVariable UUID assistantId,
                                                                  @RequestParam Integer limit) {
        return conversationService.getConversationsSummaries(null, assistantId, limit);
    }

}
