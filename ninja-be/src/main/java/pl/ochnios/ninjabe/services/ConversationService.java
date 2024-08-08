package pl.ochnios.ninjabe.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.repositories.AssistantEntityRepository;
import pl.ochnios.ninjabe.repositories.ConversationRepository;
import pl.ochnios.ninjabe.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final AssistantEntityRepository assistantEntityRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationMapper conversationMapper;

    public ConversationDto getConversation(UUID userId, UUID assistantId, UUID conversationId) {
        var conversation =
                conversationRepository.findConversationByUserIdAndAssistantIdAndIdAndDeleted(
                        userId, assistantId, conversationId, false);
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

    public List<ConversationSummaryDto> getConversationsSummaries(
            UUID userId, UUID assistantId, Integer limit) {
        var pageable = PageRequest.of(0, limit != null && limit > 0 && limit < 100 ? limit : 50);
        var conversationsPage =
                conversationRepository.findAllByUserIdAndAssistantIdAndDeletedOrderByCreatedAtDesc(
                        userId, assistantId, false, pageable);
        return conversationsPage.getContent().stream().map(conversationMapper::mapSummary).toList();
    }

    protected Conversation findOrCreateConversation(
            UUID userId, UUID assistantId, UUID conversationId) {
        if (conversationId == null) {
            return startConversation(assistantId);
        } else {
            var conversation =
                    conversationRepository.findConversationByUserIdAndAssistantIdAndIdAndDeleted(
                            userId, assistantId, conversationId, false);
            return conversation.orElseGet(() -> startConversation(assistantId));
        }
    }

    private Conversation startConversation(UUID assistantId) {
        return conversationRepository.save(
                Conversation.builder()
                        .assistant(assistantEntityRepository.findById(assistantId).orElse(null))
                        .summary("Some conversation...") // TODO auto generate using LLM
                        .messages(new ArrayList<>())
                        .deleted(false)
                        .build());
    }
}
