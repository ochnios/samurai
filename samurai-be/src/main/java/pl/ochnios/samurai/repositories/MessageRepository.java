package pl.ochnios.samurai.repositories;

import java.util.List;
import java.util.UUID;
import pl.ochnios.samurai.model.entities.conversation.MessageEntity;

public interface MessageRepository {

    List<MessageEntity> findAllByConversationId(UUID conversationId);
}
