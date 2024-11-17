package pl.ochnios.samurai.model.dtos.chat;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.ochnios.samurai.model.dtos.conversation.MessageSourceDto;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "${docs.dto.chat-response}")
public class ChatResponseDto {

    @Schema(description = "${docs.dto.chat-response.conversationId}", accessMode = READ_ONLY)
    private final UUID conversationId;

    @Schema(description = "${docs.dto.chat-response.messageId}", accessMode = READ_ONLY)
    private final UUID messageId;

    @Schema(description = "${docs.dto.chat-response.summary}")
    private final String summary;

    @Schema(description = "${docs.dto.chat-response.completion}", accessMode = READ_ONLY)
    private final String completion;

    @Schema(description = "${docs.dto.message.sources}", accessMode = READ_ONLY)
    private List<MessageSourceDto> sources;
}
