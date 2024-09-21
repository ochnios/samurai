package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.ochnios.ninjabe.commons.patch.JsonPatchService;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.model.mappers.MessageMapper;
import pl.ochnios.ninjabe.model.mappers.PageMapper;
import pl.ochnios.ninjabe.repositories.ConversationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.JsonPatch;

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
        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        return conversationMapper.map(conversation);
    }

    @Transactional(readOnly = true)
    public PageDto<ConversationSummaryDto> getSummariesPage(
            User user, PageRequestDto pageRequestDto) {
        final var pageRequest = pageMapper.map(pageRequestDto);
        final var conversationsPage = conversationRepository.findAllByUser(user, pageRequest);
        return pageMapper.map(conversationsPage, conversationMapper::mapSummary);
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

    @Transactional
    public ConversationDto patchConversation(User user, UUID conversationId, JsonPatch jsonPatch) {
        final var conversation = conversationRepository.findByUserAndId(user, conversationId);
        patchService.apply(conversation, jsonPatch);
        final var savedConversation = conversationRepository.save(conversation);
        return conversationMapper.map(savedConversation);
    }

    private Conversation createConversation(User user, String summary) {
        return Conversation.builder()
                .user(user)
                .summary(summary)
                .messages(new ArrayList<>())
                .build();
    }
}
