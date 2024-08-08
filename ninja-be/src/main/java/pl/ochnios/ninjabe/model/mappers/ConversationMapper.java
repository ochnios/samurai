package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.Message;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(source = "assistant.id", target = "assistantId")
    ConversationDto map(Conversation conversation);

    ConversationSummaryDto mapSummary(Conversation conversation);

    MessageDto mapMessage(Message message);
}
