package pl.ochnios.samurai.services.chat;

import static pl.ochnios.samurai.services.chat.Prompts.CHAT_PROMPT;
import static pl.ochnios.samurai.services.chat.Prompts.CONVERSATION_SUMMARY_PROMPT;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
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

    private final ChatContext chatContext;
    private final ChatClientProvider chatClientProvider;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    public ChatResponseDto getCompletion(User user, ChatRequestDto chatRequestDto) {
        var conversationDto = getConversation(user, chatRequestDto);

        var userMessage = MessageDto.user(chatRequestDto.getQuestion());
        conversationService.saveMessage(user, conversationDto.getId(), userMessage);

        chatContext.addMessage(userMessage.getContent());
        var messageHistory = getMessageHistory(conversationDto);
        var chatResponse = getChatResponse(messageHistory, chatRequestDto.getQuestion());

        var completion = chatResponse.getResult().getOutput().getContent();
        var assistantMessage = MessageDto.assistant(completion);
        var savedAssistantMessage = conversationService.saveMessage(user, conversationDto.getId(), assistantMessage);

        log.info("Completion for conversation {} created", conversationDto.getId());
        return getChatResponseDto(conversationDto, savedAssistantMessage);
    }

    private ConversationDto getConversation(User user, ChatRequestDto chatRequestDto) {
        if (chatRequestDto.getConversationId() == null) {
            var summary = generateSummary(chatRequestDto.getQuestion());
            return conversationService.startConversation(user, summary);
        }

        return conversationService.getConversation(user, chatRequestDto.getConversationId());
    }

    // Get last messages which fits in maxMessageTokens window
    private List<Message> getMessageHistory(ConversationDto conversationDto) {
        var fullHistory = conversationDto.getMessages().stream()
                .map(messageMapper::mapToSpringMessage)
                .toList();

        List<Message> cutOffHistory = new ArrayList<>();
        for (int i = fullHistory.size() - 1; i >= 0; i--) {
            if (chatContext.addMessage(fullHistory.get(i).getContent())) {
                cutOffHistory.addFirst(fullHistory.get(i));
            } else {
                break;
            }
        }

        return cutOffHistory;
    }

    private ChatResponse getChatResponse(List<Message> history, String question) {
        return chatClientProvider
                .getChatClient()
                .prompt()
                .functions("getDocuments", "getDocument", "search")
                .advisors(new SimpleLoggerAdvisor())
                .system(CHAT_PROMPT) // TODO from app configuration
                .messages(history)
                .user(question)
                .call()
                .chatResponse();
    }

    private ChatResponseDto getChatResponseDto(ConversationDto conversationDto, MessageDto messageDto) {
        return ChatResponseDto.builder()
                .conversationId(conversationDto.getId())
                .messageId(messageDto.getId())
                .summary(conversationDto.getMessages().isEmpty() ? conversationDto.getSummary() : null)
                .completion(messageDto.getContent())
                .build();
    }

    private String generateSummary(String question) {
        String systemPrompt = CONVERSATION_SUMMARY_PROMPT.replace("{user_message}", question);
        return chatClientProvider
                .getTaskClient()
                .prompt()
                .advisors(new SimpleLoggerAdvisor())
                .system(systemPrompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();
    }
}
