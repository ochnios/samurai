package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;

@Mapper(uses = {MessageMapper.class, UserMapper.class})
public interface ConversationMapper {

    ConversationDto map(Conversation conversation);

    ConversationSummaryDto mapToSummary(Conversation conversation);

    ConversationDetailsDto mapToDetails(Conversation conversation);
}
