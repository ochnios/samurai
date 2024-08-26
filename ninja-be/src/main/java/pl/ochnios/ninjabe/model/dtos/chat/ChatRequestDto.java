package pl.ochnios.ninjabe.model.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatRequestDto {

    private final UUID conversationId;
    private final String question;
}
