package pl.ochnios.ninjabe.repositories.impl;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;

public interface ConversationCrudRepository
        extends CrudRepository<Conversation, UUID>, JpaSpecificationExecutor<Conversation> {

    Optional<Conversation> findByUserAndIdAndDeleted(User user, UUID conversationId, Boolean deleted);

    Page<Conversation> findAllByUserAndDeleted(User user, Pageable pageable, Boolean deleted);
}
