package pl.ochnios.ninjabe.model.dtos.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ConversationDto {

    private UUID id;
    private List<MessageDto> messages;
    private UUID assistantId;
    private UUID userId;
    private String summary;
    private String createdAt;
}
