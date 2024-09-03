package pl.ochnios.ninjabe.repositories.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import pl.ochnios.ninjabe.exceptions.ResourceNotFoundException;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.ConversationRepository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationCrudRepository conversationCrudRepository;

    @Override
    public Conversation findByUserAndId(User user, UUID id) {
        return conversationCrudRepository
                .findByUserAndId(user, id)
                .orElseThrow(() -> ResourceNotFoundException.of(Conversation.class, id));
    }

    @Override
    public Page<Conversation> findAllByUser(User user, Pageable pageable) {
        return conversationCrudRepository.findAllByUser(user, pageable);
    }

    @Override
    public Conversation save(Conversation conversation) {
        return conversationCrudRepository.save(conversation);
    }

    @Override
    public void delete(Conversation conversation) {
        conversationCrudRepository.delete(conversation);
    }
}
