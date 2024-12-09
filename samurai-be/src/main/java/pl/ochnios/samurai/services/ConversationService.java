package pl.ochnios.samurai.services;

import static pl.ochnios.samurai.model.entities.conversation.Conversation.MAX_SUMMARY_LENGTH;

import java.util.Map;
import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.samurai.commons.patch.JsonPatchService;
import pl.ochnios.samurai.model.dtos.conversation.ConversationCriteria;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.samurai.model.dtos.conversation.MessageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.entities.conversation.ConversationSpecification;
import pl.ochnios.samurai.model.entities.conversation.MessageSource;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.ConversationMapper;
import pl.ochnios.samurai.model.mappers.MessageMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.ConversationRepository;
import pl.ochnios.samurai.repositories.DocumentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final DocumentRepository documentRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Transactional(readOnly = true)
    public ConversationDto getConversationPreview(User user, UUID conversationId) {
        Conversation conversation;
        if (user.hasModRole() || user.hasAdminRole()) {
            conversation = conversationRepository.findByIdIncludingDeleted(conversationId);
        } else {
            conversation = conversationRepository.findByUserAndId(user, conversationId);
        }
        return conversationMapper.map(conversation);
    }

    @Transactional(readOnly = true)
    public ConversationDto getConversation(User user, UUID conversationId) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        return conversationMapper.map(conversation);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationSummaryDto> getSummariesPage(User user, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.map(pageRequestDto);
        var conversationsPage = conversationRepository.findAllByUser(user, pageRequest);
        return pageMapper.map(conversationsPage, conversationMapper::mapToSummary);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationDetailsDto> getDetailsPage(
            ConversationCriteria criteria, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.map(pageRequestDto);
        var specification = ConversationSpecification.create(criteria);
        var conversationsPage = conversationRepository.findAllIncludingDeleted(specification, pageRequest);
        return pageMapper.map(conversationsPage, conversationMapper::mapToDetails);
    }

    @Transactional
    public ConversationDto startConversation(User user, String summary) {
        var conversation = Conversation.builder()
                .user(user)
                .summary(validatedSummary(summary))
                .build();
        var savedConversation = conversationRepository.save(conversation);
        log.info("Conversation {} started", savedConversation.getId());
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public MessageDto saveUserMessage(User user, UUID conversationId, String content) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        var message = messageMapper.map(MessageDto.user(content), conversation);
        conversation.addMessage(message);
        var savedConversation = conversationRepository.save(conversation);
        log.info("Message {} for conversation {} saved", message.getId(), conversationId);
        return messageMapper.map(savedConversation.getMessages().getLast());
    }

    @Transactional
    public MessageDto saveAssistantMessage(
            User user, UUID conversationId, String content, Map<UUID, String> documents) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        var sources = documentRepository.findAllById(documents.keySet()).stream()
                .map(d -> MessageSource.builder()
                        .originalTitle(d.getTitle())
                        .retrievedContent(documents.get(d.getId()))
                        .document(d)
                        .build())
                .toList();
        var message = messageMapper.map(MessageDto.assistant(content), conversation, sources);
        message.getSources().forEach(m -> m.setMessage(message));
        conversation.addMessage(message);
        var savedConversation = conversationRepository.save(conversation);
        log.info("Message for conversation {} saved", conversationId);
        return messageMapper.map(savedConversation.getMessages().getLast());
    }

    @Transactional
    public ConversationDto patchConversation(User user, UUID conversationId, JsonPatch jsonPatch) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        patchService.apply(conversation, jsonPatch);
        var savedConversation = conversationRepository.save(conversation);
        log.info("Conversation {} patched", conversationId);
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public void deleteConversation(User user, UUID conversationId) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        conversationRepository.delete(conversation);
        log.info("Conversation {} deleted", conversationId);
    }

    private String validatedSummary(String summary) {
        return summary.length() > MAX_SUMMARY_LENGTH ? summary.substring(0, MAX_SUMMARY_LENGTH) : summary;
    }
}
