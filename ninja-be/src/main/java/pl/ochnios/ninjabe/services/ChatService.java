package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.dtos.chat.ChatResponseDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.Message;
import pl.ochnios.ninjabe.repositories.MessageRepository;
import pl.ochnios.ninjabe.services.ai.ChatClientProvider;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClientProvider chatClientProvider;
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    @Transactional
    public ChatResponseDto getCompletion(ChatRequestDto chatRequestDto) {
        var chatClient = chatClientProvider.getChatClient();
        var conversation =
                conversationService.findOrCreateConversation(
                        null, chatRequestDto.getConversationId());

        var prompt = preparePrompt(conversation, chatRequestDto.getQuestion());
        var completion = chatClient.prompt(prompt).call();

        var assistantMessage = Message.assistant(conversation, completion.content());
        assistantMessage = messageRepository.save(assistantMessage);

        return new ChatResponseDto(conversation.getId(), assistantMessage.getContent());
    }

    private Prompt preparePrompt(Conversation conversation, String userMessageStr) {
        var systemMessage = Message.system(conversation, "You are a helpful assistant");
        var userMessage = Message.user(conversation, userMessageStr);
        userMessage = messageRepository.save(userMessage);

        var messages =
                messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversation.getId());
        messages.add(0, systemMessage);

        return new Prompt(
                messages.stream()
                        .map(m -> (org.springframework.ai.chat.messages.Message) m)
                        .collect(Collectors.toList()));
    }
}
