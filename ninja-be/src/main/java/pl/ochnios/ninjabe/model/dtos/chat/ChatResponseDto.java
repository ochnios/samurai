package pl.ochnios.ninjabe.model.dtos.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.chat-response}")
public class ChatResponseDto {

    @Schema(description = "${docs.dto.chat-response.conversationId}", accessMode = READ_ONLY)
    private final UUID conversationId;

    @Schema(description = "${docs.dto.chat-response.summary}")
    private final String summary;

    @Schema(description = "${docs.dto.chat-response.completion}", accessMode = READ_ONLY)
    private final String completion;
}
