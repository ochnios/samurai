package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;

@Mapper(
        componentModel = "spring",
        uses = {MessageMapper.class})
public interface ConversationMapper {

    @Mapping(target = "username", source = "user.username")
    ConversationDto map(Conversation conversation);

    ConversationSummaryDto mapSummary(Conversation conversation);
}
