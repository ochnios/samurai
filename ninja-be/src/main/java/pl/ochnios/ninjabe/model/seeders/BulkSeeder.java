package pl.ochnios.ninjabe.model.seeders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;
import pl.ochnios.ninjabe.model.entities.user.Role;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@Component
@RequiredArgsConstructor
public class BulkSeeder implements DataSeeder {

    private static final int USER_COUNT = 100;
    private static final int CONVERSATIONS_PER_USER = 10;

    private final Faker faker = new Faker();
    private final PasswordEncoder passwordEncoder;
    private final UserCrudRepository userCrudRepository;

    @Override
    public void seed() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < USER_COUNT; i++) {
            var user = fakeUser();
            for (int j = 0; j < CONVERSATIONS_PER_USER; j++) {
                var conversation = fakeConversation(user);
                for (int k = 0; k < faker.number().numberBetween(2, 11); k++) {
                    var type = k % 2 == 0 ? MessageType.USER : MessageType.ASSISTANT;
                    conversation.addMessage(fakeMessageEntity(conversation, type));
                }
                user.getConversations().add(conversation);
            }
            users.add(user);
        }
        userCrudRepository.saveAll(users);
    }

    private User fakeUser() {
        return User.builder()
                .username(faker.internet().username())
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(passwordEncoder.encode(faker.internet().password()))
                .role(Role.User)
                .build();
    }

    private Conversation fakeConversation(User user) {
        return Conversation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .summary(createSummary())
                .deleted(faker.bool().bool())
                .build();
    }

    private MessageEntity fakeMessageEntity(Conversation conversation, MessageType type) {
        return MessageEntity.builder()
                .id(UUID.randomUUID())
                .conversation(conversation)
                .content(faker.lorem().paragraph())
                .type(type)
                .build();
    }

    private String createSummary() {
        String summary = String.join(" ", faker.lorem().words(3));
        return summary.substring(0, 1).toUpperCase() + summary.substring(1);
    }
}
