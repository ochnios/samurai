package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.ninjabe.assistant.Assistant;
import pl.ochnios.ninjabe.assistant.AssistantRegistry;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.model.entities.chat.Conversation;
import pl.ochnios.ninjabe.model.entities.chat.Message;
import pl.ochnios.ninjabe.repositories.AssistantEntityRepository;
import pl.ochnios.ninjabe.repositories.ConversationRepository;
import pl.ochnios.ninjabe.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AssistantRegistry assistantRegistry;
    private final AssistantEntityRepository assistantEntityRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatResponseDto getCompletion(UUID assistantId, ChatRequestDto chatRequestDto) {
        var assistant = assistantRegistry.get(assistantId);
        var conversation = findOrCreateConversation(assistantId, chatRequestDto.getConversationId());

        var prompt = preparePrompt(assistant, conversation, chatRequestDto.getQuestion());
        var completion = assistant.chat().call(prompt);

        var assistantMessage = Message.assistantt(conversation, completion.getResult().getOutput().getContent());
        assistantMessage = messageRepository.save(assistantMessage);

        return new ChatResponseDto(conversation.getId(), assistantMessage.getContent());
    }

    private Conversation findOrCreateConversation(UUID assistantId, UUID conversationId) {
        if (conversationId == null) {
            return startConversation(assistantId);
        } else {
            return conversationRepository
                    .findConversationByIdAndAssistantIdAndDeletedIs(conversationId, assistantId, false)
                    .orElse(startConversation(assistantId));
        }
    }

    private Conversation startConversation(UUID assistantId) {
        return conversationRepository.save(Conversation.builder()
                .assistant(assistantEntityRepository.findById(assistantId).orElse(null))
                .messages(new ArrayList<>())
                .deleted(false)
                .build());
    }

    private Prompt preparePrompt(Assistant assistant, Conversation conversation, String userMessageStr) {
        var systemMessage = Message.system(conversation, assistant.config().getSystemPrompt());
        var userMessage = Message.user(conversation, userMessageStr);
        userMessage = messageRepository.save(userMessage);

        var messages = conversation.getMessages();
        messages.add(0, systemMessage);
        messages.add(userMessage);

        return new Prompt(messages.stream()
                .map(m -> (org.springframework.ai.chat.messages.Message) m)
                .collect(Collectors.toList()));
    }
}
