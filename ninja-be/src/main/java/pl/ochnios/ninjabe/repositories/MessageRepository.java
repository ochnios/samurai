package pl.ochnios.ninjabe.repositories;

import java.util.List;
import java.util.UUID;
import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;

public interface MessageRepository {

    List<MessageEntity> findAllByConversationId(UUID conversationId);

    MessageEntity save(MessageEntity messageEntity);
}
