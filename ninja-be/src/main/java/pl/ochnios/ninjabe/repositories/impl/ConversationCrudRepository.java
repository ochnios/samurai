package pl.ochnios.ninjabe.repositories.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;

import java.util.Optional;
import java.util.UUID;

public interface ConversationCrudRepository extends CrudRepository<Conversation, UUID> {

    Optional<Conversation> findByUserAndId(User user, UUID conversationId);

    Page<Conversation> findAllByUser(User user, Pageable pageable);
}
