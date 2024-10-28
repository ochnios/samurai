package pl.ochnios.samurai.repositories.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.repositories.ConversationRepository;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationCrudRepository conversationCrudRepository;

    @Override
    public Conversation findByUserAndId(User user, UUID conversationId) {
        return conversationCrudRepository
                .findByUserAndIdAndDeleted(user, conversationId, false)
                .orElseThrow(() -> ResourceNotFoundException.of(Conversation.class, conversationId));
    }

    @Override
    public Conversation findByIdIncludingDeleted(UUID conversationId) {
        return conversationCrudRepository
                .findById(conversationId)
                .orElseThrow(() -> ResourceNotFoundException.of(Conversation.class, conversationId));
    }

    @Override
    public Page<Conversation> findAllByUser(User user, Pageable pageable) {
        return conversationCrudRepository.findAllByUserAndDeleted(user, pageable, false);
    }

    @Override
    public Page<Conversation> findAllIncludingDeleted(Specification<Conversation> specification, Pageable pageable) {
        return conversationCrudRepository.findAll(specification, pageable);
    }

    @Override
    public Conversation save(Conversation conversation) {
        return conversationCrudRepository.save(conversation);
    }

    @Override
    public void delete(Conversation conversation) {
        conversation.setDeleted(true);
        for (var message : conversation.getMessages()) {
            message.setDeleted(true);
        }
        conversationCrudRepository.save(conversation);
    }
}
