package pl.ochnios.ninjabe.model.dtos.chat;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponseDto {

    private final UUID conversationId;
    private final String completion;
}
