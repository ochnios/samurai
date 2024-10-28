package pl.ochnios.samurai.model.seeders;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.entities.conversation.MessageEntity;
import pl.ochnios.samurai.repositories.ConversationRepository;
import pl.ochnios.samurai.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public void seed() {
        final var conversationId = UUID.nameUUIDFromBytes("c1".getBytes());
        final var user = userRepository.findByUsername("user");
        final var conversation = Conversation.builder()
                .id(conversationId)
                .user(user)
                .summary("New conversation")
                .build();
        conversation.addMessages(createMessages(conversation));
        final var savedConversation = conversationRepository.save(conversation);

        log.info("Created conversation: {}", savedConversation);
        log.info("Created messages: {}", savedConversation.getMessages());
    }

    private List<MessageEntity> createMessages(Conversation conversation) {
        final var userMessage = MessageEntity.builder()
                .id(UUID.nameUUIDFromBytes("m1".getBytes()))
                .conversation(conversation)
                .content("Hello!")
                .type(MessageType.USER)
                .build();
        final var assistantMessage = MessageEntity.builder()
                .id(UUID.nameUUIDFromBytes("m2".getBytes()))
                .conversation(conversation)
                .content("Hi there! How can I help you today?")
                .type(MessageType.ASSISTANT)
                .build();
        return List.of(userMessage, assistantMessage);
    }
}
