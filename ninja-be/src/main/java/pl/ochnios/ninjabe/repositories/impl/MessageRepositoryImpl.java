package pl.ochnios.ninjabe.repositories.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;
import pl.ochnios.ninjabe.repositories.MessageRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageCrudRepository messageCrudRepository;

    @Override
    public List<MessageEntity> findAllByConversationId(UUID conversationId) {
        return messageCrudRepository.findAllByConversationId(conversationId);
    }

    @Override
    public MessageEntity save(MessageEntity messageEntity) {
        return messageCrudRepository.save(messageEntity);
    }
}
