package pl.ochnios.ninjabe.model.dtos.chat;

import lombok.Data;

@Data
public class ChatRequestDto {

    String conversationId;
    String question;
}
