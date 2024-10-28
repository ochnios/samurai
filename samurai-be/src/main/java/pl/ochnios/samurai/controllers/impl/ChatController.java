package pl.ochnios.samurai.controllers.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.controllers.ChatApi;
import pl.ochnios.samurai.model.dtos.chat.ChatRequestDto;
import pl.ochnios.samurai.model.dtos.chat.ChatResponseDto;
import pl.ochnios.samurai.services.ChatService;
import pl.ochnios.samurai.services.security.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController implements ChatApi {

    private final AuthService authService;
    private final ChatService chatService;

    @Override
    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatRequestDto chatRequestDto) {
        final var user = authService.getAuthenticatedUser();
        final var chatResponse = chatService.getCompletion(user, chatRequestDto);
        return ResponseEntity.ok(chatResponse);
    }
}
