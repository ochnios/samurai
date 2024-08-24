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

    Optional<Conversation> findConversationByUserIdAndIdAndDeleted(
            UUID userId, UUID conversationId, Boolean deleted);

    Page<Conversation> findAllByUserIdAndDeletedOrderByCreatedAtDesc(
            UUID userId, Boolean deleted, Pageable pageable);
}
