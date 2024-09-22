package pl.ochnios.ninjabe.model.dtos.conversation;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;

@Data
@AllArgsConstructor
public class MessageDto {

    private UUID id;
    private String content;
    private MessageType type;
    private String createdAt;

    public static MessageDto message(String content, MessageType type) {
        return new MessageDto(null, content, type, null);
    }

    public static MessageDto user(String content) {
        return message(content, MessageType.USER);
    }

    public static MessageDto assistant(String content) {
        return message(content, MessageType.ASSISTANT);
    }
}
