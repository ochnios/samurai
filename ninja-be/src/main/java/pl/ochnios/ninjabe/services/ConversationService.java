package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.model.mappers.MessageMapper;
import pl.ochnios.ninjabe.repositories.ConversationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Transactional(readOnly = true)
    public ConversationDto getConversation(User user, UUID conversationId) {
        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        return conversationMapper.map(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> getSummaries(User user, Integer limit) {
        final var validatedLimit = limit != null && limit > 0 && limit < 100 ? limit : 50;
        final var pageable = PageRequest.of(0, validatedLimit, Sort.by("createdAt").ascending());
        final var conversationsPage = conversationRepository.findAllByUser(user, pageable);
        return conversationsPage.getContent().stream().map(conversationMapper::mapSummary).toList();
    }

    @Transactional
    public ConversationDto startConversation(User user, String summary) {
        final var conversation = createConversation(user, summary);
        final var savedConversation = conversationRepository.save(conversation);
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public void saveMessages(User user, UUID conversationId, List<MessageDto> messageDtos) {
        if (messageDtos == null || messageDtos.isEmpty()) return;

        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        final var messages = messageMapper.map(conversation, messageDtos);
        conversation.getMessages().addAll(messages);
        conversationRepository.save(conversation);
    }

    private Conversation createConversation(User user, String summary) {
        return Conversation.builder()
                .user(user)
                .summary(summary)
                .messages(new ArrayList<>())
                .build();
    }
}
