package pl.ochnios.ninjabe.model.dtos.conversation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.ninjabe.model.entities.chat.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(source = "id", target = "conversationId")
    ConversationSummaryDto map(Conversation conversation);
}
