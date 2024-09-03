package pl.ochnios.ninjabe.model.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatResponseDto {

    private final UUID conversationId;
    private final String completion;
}
