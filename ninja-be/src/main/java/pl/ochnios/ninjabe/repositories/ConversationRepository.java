package pl.ochnios.ninjabe.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.ninjabe.model.entities.chat.Conversation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CrudRepository<Conversation, UUID> {

    Optional<Conversation> findConversationByIdAndAssistantIdAndDeletedIs(UUID conversationId, UUID assistantId,
                                                                          Boolean deleted);
}
