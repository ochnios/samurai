package pl.ochnios.samurai.services.chat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.model.dtos.chat.ChatRequestDto;
import pl.ochnios.samurai.model.dtos.chat.ChatResponseDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDto;
import pl.ochnios.samurai.model.dtos.conversation.MessageDto;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.MessageMapper;
import pl.ochnios.samurai.services.ConversationService;
import pl.ochnios.samurai.services.chat.advisors.SimpleLoggerAdvisor;
import pl.ochnios.samurai.services.chat.provider.ChatClientProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClientProvider chatClientProvider;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    public ChatResponseDto getCompletion(User user, ChatRequestDto chatRequestDto) {
        var conversationDto = getConversation(user, chatRequestDto);
        var springMessages = getSpringMessages(conversationDto);

        var userMessage = MessageDto.user(chatRequestDto.getQuestion());
        conversationService.saveMessage(user, conversationDto.getId(), userMessage);

        var chatResponse = chatClientProvider
                .getChatClient()
                .prompt()
                .functions("search")
                .advisors(new SimpleLoggerAdvisor())
                .system(
                        """
You are a knowledgeable assistant with access to a document retrieval system.
Your goal is to provide accurate information based on the available knowledge base.

1. Use the search tool to find relevant information before responding to any use questions.
2. Base your answers on retrieved documents, quoting specific passages when necessary.
3. If no relevant information is found, state: "I don't have specific information about this in my knowledge base."
4. Be transparent about the sources of your information and acknowledge any limitations.
5. Avoid making assumptions; prioritize accuracy over completeness.

Your primary duty is to provide truthful information, acknowledging gaps when they exist.
""") // TODO from app configuration
                .messages(springMessages)
                .user(chatRequestDto.getQuestion())
                .call()
                .chatResponse();

        var completion = chatResponse.getResult().getOutput().getContent();
        var assistantMessage = MessageDto.assistant(completion);
        var savedAssistantMessage = conversationService.saveMessage(user, conversationDto.getId(), assistantMessage);

        log.info("Completion for conversation {} created", conversationDto.getId());
        return getChatResponse(conversationDto, savedAssistantMessage);
    }

    private ConversationDto getConversation(User user, ChatRequestDto chatRequestDto) {
        if (chatRequestDto.getConversationId() == null) {
            var summary = generateSummary(chatRequestDto.getQuestion());
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
