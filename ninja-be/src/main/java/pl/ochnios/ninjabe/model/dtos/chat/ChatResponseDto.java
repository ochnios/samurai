package pl.ochnios.ninjabe.model.dtos.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponseDto {

    String conversationId;
    String completion;
}
