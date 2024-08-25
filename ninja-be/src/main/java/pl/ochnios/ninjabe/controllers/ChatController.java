package pl.ochnios.ninjabe.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.services.ChatService;
import pl.ochnios.ninjabe.services.UserService;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto chatRequestDto) {
        final var user = userService.getCurrentUser();
        return chatService.getCompletion(user, chatRequestDto);
    }
}
