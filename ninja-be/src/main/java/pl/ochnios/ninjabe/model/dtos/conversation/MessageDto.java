package pl.ochnios.ninjabe.model.dtos.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.ai.chat.messages.MessageType;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MessageDto {

    private UUID id;
    private String content;
    private MessageType type;
    private String createdAt;
}
