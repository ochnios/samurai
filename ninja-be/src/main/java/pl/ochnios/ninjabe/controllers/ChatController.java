package pl.ochnios.ninjabe.controllers;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.security.AuthService;
import pl.ochnios.ninjabe.services.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final AuthService authService;
    private final ChatService chatService;

    @PostMapping
    public ChatResponseDto chat(@Valid @RequestBody ChatRequestDto chatRequestDto) {
        final var user = authService.getAuthenticatedUser();
        return chatService.getCompletion(user, chatRequestDto);
    }
}
