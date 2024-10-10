package pl.ochnios.ninjabe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.ninjabe.commons.patch.JsonPatchService;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationCriteria;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.ConversationSpecification;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.model.mappers.MessageMapper;
import pl.ochnios.ninjabe.model.mappers.PageMapper;
import pl.ochnios.ninjabe.repositories.ConversationRepository;

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
        final var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        final var conversationsPage = conversationRepository.findAllByUser(user, pageRequest);
        return pageMapper.validOrDefaultSort(conversationsPage, conversationMapper::mapToSummary);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationDetailsDto> getDetailsPage(
            ConversationCriteria criteria, PageRequestDto pageRequestDto) {
        final var pageRequest = pageMapper.validOrDefaultSort(pageRequestDto);
        final var specification = ConversationSpecification.create(criteria);
        final var conversationsPage = conversationRepository.findAllIncludingDeleted(specification, pageRequest);
        return pageMapper.validOrDefaultSort(conversationsPage, conversationMapper::mapToDetails);
    }

    @Transactional
    public ConversationDto startConversation(User user, String summary) {
        final var conversation = createConversation(user, summary);
        final var savedConversation = conversationRepository.save(conversation);
        log.info("Conversation {} started", savedConversation.getId());
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public void saveMessages(User user, UUID conversationId, List<MessageDto> messageDtos) {
        if (messageDtos == null || messageDtos.isEmpty()) return;

        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        final var messages = messageMapper.map(conversation, messageDtos);
        conversation.addMessages(messages);
        conversationRepository.save(conversation);
        log.info("Messages for conversation {} saved", conversationId);
    }

    @Transactional
    public ConversationDto patchConversation(User user, UUID conversationId, JsonPatch jsonPatch) {
        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        patchService.apply(conversation, jsonPatch);
        final var savedConversation = conversationRepository.save(conversation);
        log.info("Conversation {} patched", conversationId);
        return conversationMapper.map(savedConversation);
    }

    @Transactional
    public void deleteConversation(User user, UUID conversationId) {
        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
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
