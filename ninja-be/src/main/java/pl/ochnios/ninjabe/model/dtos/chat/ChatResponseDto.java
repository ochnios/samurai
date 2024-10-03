package pl.ochnios.ninjabe.model.dtos.chat;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.chat-response}")
public class ChatResponseDto {

    @Schema(description = "${docs.dto.chat-response.conversationId}", accessMode = READ_ONLY)
    private final UUID conversationId;

    @Schema(description = "${docs.dto.chat-response.completion}", accessMode = READ_ONLY)
    private final String completion;
}
