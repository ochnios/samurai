package pl.ochnios.ninjabe.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.repositories.ConversationRepository;
import pl.ochnios.ninjabe.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationMapper conversationMapper;

    public ConversationDto getConversation(UUID userId, UUID conversationId) {
        var conversation =
                conversationRepository.findConversationByUserIdAndIdAndDeleted(
                        userId, conversationId, false);

        if (conversation.isPresent()) {
            var conversationDto = conversationMapper.map(conversation.get());
            var messages =
                    messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId);
            conversationDto.setMessages(
                    messages.stream().map(conversationMapper::mapMessage).toList());
            return conversationDto;
        } else
            throw new EntityNotFoundException(
                    "Conversation with id " + conversationId + " not found");
    }

    public List<ConversationSummaryDto> getConversationsSummaries(UUID userId, Integer limit) {
        var pageable = PageRequest.of(0, limit != null && limit > 0 && limit < 100 ? limit : 50);
        var conversationsPage =
                conversationRepository.findAllByUserIdAndDeletedOrderByCreatedAtDesc(
                        userId, false, pageable);
        return conversationsPage.getContent().stream().map(conversationMapper::mapSummary).toList();
    }

    protected Conversation findOrCreateConversation(UUID userId, UUID conversationId) {
        if (conversationId == null) {
            return startConversation();
        } else {
            var conversation =
                    conversationRepository.findConversationByUserIdAndIdAndDeleted(
                            userId, conversationId, false);
            return conversation.orElseGet(this::startConversation);
        }
    }

    private Conversation startConversation() {
        return conversationRepository.save(
                Conversation.builder()
                        .summary("New conversation")
                        .messages(new ArrayList<>())
                        .deleted(false)
                        .build());
    }
}
