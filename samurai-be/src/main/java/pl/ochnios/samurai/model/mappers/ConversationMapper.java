package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.samurai.model.entities.conversation.Conversation;

@Mapper(uses = { MessageMapper.class, UserMapper.class })
public interface ConversationMapper {

    ConversationDto map(Conversation conversation);

    ConversationSummaryDto mapToSummary(Conversation conversation);

    ConversationDetailsDto mapToDetails(Conversation conversation);
}
