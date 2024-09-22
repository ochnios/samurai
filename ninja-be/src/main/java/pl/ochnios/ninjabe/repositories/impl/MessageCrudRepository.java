package pl.ochnios.ninjabe.repositories.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;

public interface MessageCrudRepository extends CrudRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByConversationIdAndDeleted(UUID conversationId, Boolean deleted);
}
