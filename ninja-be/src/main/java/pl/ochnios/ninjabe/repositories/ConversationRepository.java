package pl.ochnios.ninjabe.repositories;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;

public interface ConversationRepository {

    Conversation findByUserAndId(User user, UUID conversationId);

    Conversation findByIdIncludingDeleted(UUID conversationId);

    Page<Conversation> findAllByUser(User user, Pageable pageable);

    Page<Conversation> findAllIncludingDeleted(Specification<Conversation> specification, Pageable pageable);

    Conversation save(Conversation conversation);

    void delete(Conversation conversation);
}
