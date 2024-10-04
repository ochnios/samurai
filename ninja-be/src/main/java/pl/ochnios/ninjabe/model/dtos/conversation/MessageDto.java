package pl.ochnios.ninjabe.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.message}")
public class MessageDto {

    @Schema(description = "${docs.dto.message.id}", accessMode = READ_ONLY)
    private UUID id;

    @Schema(description = "${docs.dto.message.content}", accessMode = READ_ONLY)
    private String content;

    @Schema(description = "${docs.dto.message.type}", accessMode = READ_ONLY)
    private MessageType type;

    @Schema(description = "${docs.dto.message.createdAt}", accessMode = READ_ONLY)
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
