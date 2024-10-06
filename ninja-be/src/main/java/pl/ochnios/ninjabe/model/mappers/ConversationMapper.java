package pl.ochnios.ninjabe.model.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDetailsDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationDto;
import pl.ochnios.ninjabe.model.dtos.conversation.ConversationSummaryDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;

@Mapper(uses = {MessageMapper.class, UserMapper.class})
public interface ConversationMapper {

    ConversationDto map(Conversation conversation);

    ConversationSummaryDto mapToSummary(Conversation conversation);

    @Mapping(target = "messageCount", source = "messages", qualifiedByName = "mapListSize")
    ConversationDetailsDto mapToDetails(Conversation conversation);

    @Named("mapListSize")
    default int mapListSize(List<?> items) {
        return items == null ? 0 : items.size();
    }
}
