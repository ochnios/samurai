package pl.ochnios.ninjabe.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.MessageMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClientProvider chatClientProvider;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    public ChatResponseDto getCompletion(User user, ChatRequestDto chatRequestDto) {
        final var conversationDto = getConversation(user, chatRequestDto);
        final var springMessages = getSpringMessages(conversationDto);

        final var userMessage = MessageDto.user(chatRequestDto.getQuestion());
        conversationService.saveMessage(user, conversationDto.getId(), userMessage);

        final var chatResponse = chatClientProvider
                .getChatClient()
                .prompt()
                .system("You are a helpful assistant") // TODO from app configuration
                .messages(springMessages)
                .user(chatRequestDto.getQuestion())
                .call()
                .chatResponse();

        final var completion = chatResponse.getResult().getOutput().getContent();
        final var assistantMessage = MessageDto.assistant(completion);
        final var savedAssistantMessage =
                conversationService.saveMessage(user, conversationDto.getId(), assistantMessage);

        log.info("Completion for conversation {} created", conversationDto.getId());
        return getChatResponse(conversationDto, savedAssistantMessage);
    }

    private ConversationDto getConversation(User user, ChatRequestDto chatRequestDto) {
        if (chatRequestDto.getConversationId() == null) {
            final var summary = generateSummary(chatRequestDto.getQuestion());
            return conversationService.startConversation(user, summary);
        }

        return conversationService.getConversation(user, chatRequestDto.getConversationId());
    }

    private List<Message> getSpringMessages(ConversationDto conversationDto) {
        return conversationDto.getMessages().stream()
                .map(messageMapper::mapToSpringMessage)
                .toList();
    }

    private ChatResponseDto getChatResponse(ConversationDto conversationDto, MessageDto messageDto) {
        return ChatResponseDto.builder()
                .conversationId(conversationDto.getId())
                .messageId(messageDto.getId())
                .summary(conversationDto.getMessages().isEmpty() ? conversationDto.getSummary() : null)
                .completion(messageDto.getContent())
                .build();
    }

    private String generateSummary(String question) {
        return "New conversation"; // TODO generate with AI basing on question
    }
}
