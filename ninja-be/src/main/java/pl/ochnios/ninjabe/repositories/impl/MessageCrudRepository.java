package pl.ochnios.ninjabe.repositories.impl;

import org.springframework.data.repository.CrudRepository;

import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;

import java.util.List;
import java.util.UUID;

public interface MessageCrudRepository extends CrudRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByConversationId(UUID conversationId);
}
