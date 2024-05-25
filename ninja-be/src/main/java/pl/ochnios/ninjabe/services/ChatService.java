package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.ninjabe.assistant.Assistant;
import pl.ochnios.ninjabe.assistant.AssistantRegistry;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.Message;
import pl.ochnios.ninjabe.repositories.MessageRepository;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AssistantRegistry assistantRegistry;
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    @Transactional
    public ChatResponseDto getCompletion(UUID assistantId, ChatRequestDto chatRequestDto) {
        var assistant = assistantRegistry.get(assistantId);
        var conversation = conversationService
                .findOrCreateConversation(assistantId, chatRequestDto.getConversationId());

        var prompt = preparePrompt(assistant, conversation, chatRequestDto.getQuestion());
        var completion = assistant.chat().call(prompt);

        var assistantMessage = Message.assistantt(conversation, completion.getResult().getOutput().getContent());
        assistantMessage = messageRepository.save(assistantMessage);

        return new ChatResponseDto(conversation.getId(), assistantMessage.getContent());
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
