package pl.ochnios.ninjabe.model.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import pl.ochnios.ninjabe.model.dtos.conversation.MessageDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.entities.conversation.MessageEntity;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDto map(MessageEntity messageEntity);

    @Mapping(target = "id", source = "messageDto.id")
    @Mapping(target = "createdAt", source = "messageDto.createdAt")
    @Mapping(target = "conversation", source = "conversation")
    MessageEntity map(Conversation conversation, MessageDto messageDto);

    default List<MessageEntity> map(Conversation conversation, List<MessageDto> messageDtos) {
        return messageDtos.stream().map(m -> map(conversation, m)).toList();
    }

    default Message mapToSpringMessage(MessageDto messageDto) {
        return switch (messageDto.getType()) {
            case USER -> new UserMessage(messageDto.getContent());
            case SYSTEM -> new SystemMessage(messageDto.getContent());
            case ASSISTANT -> new AssistantMessage(messageDto.getContent());
            default -> throw new IllegalArgumentException(
                    "Unsupported message type: " + messageDto.getType().getValue());
        };
    }
}
