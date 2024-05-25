package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(source = "id", target = "conversationId")
    ConversationSummaryDto map(Conversation conversation);
}
