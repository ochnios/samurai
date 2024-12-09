package pl.ochnios.samurai.model.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import pl.ochnios.samurai.model.dtos.conversation.MessageDto;
import pl.ochnios.samurai.model.dtos.conversation.MessageSourceDto;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.entities.conversation.MessageEntity;
import pl.ochnios.samurai.model.entities.conversation.MessageSource;

@Mapper
public interface MessageMapper {

    @Mapping(target = "sources", source = "sources", qualifiedByName = "mapSources")
    MessageDto map(MessageEntity messageEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "conversation", source = "conversation")
    @Mapping(target = "sources", source = "sources")
    MessageEntity map(MessageDto messageDto, Conversation conversation, List<MessageSource> sources);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "conversation", source = "conversation")
    @Mapping(target = "sources", ignore = true)
    MessageEntity map(MessageDto messageDto, Conversation conversation);

    default Message mapToSpringMessage(MessageDto messageDto) {
        return switch (messageDto.getType()) {
            case USER -> new UserMessage(messageDto.getContent());
            case SYSTEM -> new SystemMessage(messageDto.getContent());
            case ASSISTANT -> new AssistantMessage(messageDto.getContent());
            default -> throw new IllegalArgumentException(
                    "Unsupported message type: " + messageDto.getType().getValue());
        };
    }

    @Named("mapSources")
    default List<MessageSourceDto> mapSources(List<MessageSource> sources) {
        if (sources == null) {
            return null;
        }

        return sources.stream()
                .map(s -> MessageSourceDto.builder()
                        .id(s.getId())
                        .originalTitle(s.getOriginalTitle())
                        .retrievedContent(s.getRetrievedContent())
                        .documentId(s.getDocument() != null ? s.getDocument().getId() : null)
                        .updated(s.getDocument() != null
                                && s.getDocument().getUpdatedAt().isAfter(s.getAccessedAt()))
                        .deleted(s.getDocument() == null)
                        .build())
                .toList();
    }
}
