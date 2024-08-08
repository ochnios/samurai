package pl.ochnios.ninjabe.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.ochnios.ninjabe.model.entities.conversation.Message;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    List<Message> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId);
}
