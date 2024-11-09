package pl.ochnios.samurai.services;

import java.util.ArrayList;
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
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.ConversationMapper;
import pl.ochnios.samurai.model.mappers.MessageMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.repositories.ConversationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Transactional(readOnly = true)
    public ConversationDto getConversation(User user, UUID conversationId) {
        Conversation conversation;
        if (user.hasModRole() || user.hasAdminRole()) {
            conversation = conversationRepository.findByIdIncludingDeleted(conversationId);
        } else {
            conversation = conversationRepository.findByUserAndId(user, conversationId);
        }
        return conversationMapper.map(conversation);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationSummaryDto> getSummariesPage(User user, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        var conversationsPage = conversationRepository.findAllByUser(user, pageRequest);
        return pageMapper.validOrDefaultSort(conversationsPage, conversationMapper::mapToSummary);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationDetailsDto> getDetailsPage(
            ConversationCriteria criteria, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        var specification = ConversationSpecification.create(criteria);
        var conversationsPage = conversationRepository.findAllIncludingDeleted(specification, pageRequest);
        return pageMapper.validOrDefaultSort(conversationsPage, conversationMapper::mapToDetails);
    }

    @Transactional
    public ConversationDto startConversation(User user, String summary) {
        var conversation = createConversation(user, summary);
        var savedConversation = conversationRepository.save(conversation);
        log.info("Conversation {} started", savedConversation.getId());
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public MessageDto saveMessage(User user, UUID conversationId, MessageDto messageDto) {
        var conversation = conversationRepository.findByUserAndId(user, conversationId);
        var message = messageMapper.map(conversation, messageDto);
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

    private Conversation createConversation(User user, String summary) {
        return Conversation.builder()
                .user(user)
                .summary(summary)
                .messages(new ArrayList<>())
                .build();
    }
}
