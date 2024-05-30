package pl.ochnios.ninjabe.model.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatRequestDto {

    UUID conversationId;
    String question;
}
