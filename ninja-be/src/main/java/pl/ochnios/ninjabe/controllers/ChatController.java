package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.services.ChatService;

import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/assistants/{assistantId}/chat")
    public ChatResponseDto chat(@PathVariable UUID assistantId, @RequestBody ChatRequestDto chatRequestDto) {
        // TODO: Add user id to method signature
        return chatService.getCompletion(assistantId, chatRequestDto);
    }

}
