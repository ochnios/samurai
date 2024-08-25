package pl.ochnios.ninjabe.repositories;

import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    List<MessageEntity> findAllByConversationId(UUID conversationId);

    MessageEntity save(MessageEntity messageEntity);
}
