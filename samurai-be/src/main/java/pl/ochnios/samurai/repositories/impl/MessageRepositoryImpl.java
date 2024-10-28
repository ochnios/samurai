package pl.ochnios.samurai.repositories.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.model.entities.conversation.MessageEntity;
import pl.ochnios.samurai.repositories.MessageRepository;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageCrudRepository messageCrudRepository;

    @Override
    public List<MessageEntity> findAllByConversationId(UUID conversationId) {
        return messageCrudRepository.findAllByConversationIdAndDeleted(conversationId, false);
    }
}
