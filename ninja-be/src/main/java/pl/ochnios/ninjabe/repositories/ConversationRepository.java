package pl.ochnios.ninjabe.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CrudRepository<Conversation, UUID> {

    Optional<Conversation> findConversationByIdAndAssistantIdAndDeletedIs(UUID conversationId, UUID assistantId,
                                                                          Boolean deleted);

    Page<Conversation> findAllByUserIdAndAssistantIdAndDeletedOrderByCreatedAtDesc(UUID userId, UUID assistantId,
                                                                                   Boolean deleted,
                                                                                   Pageable pageable);

}
