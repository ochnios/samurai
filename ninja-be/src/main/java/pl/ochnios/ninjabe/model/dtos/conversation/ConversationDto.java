package pl.ochnios.ninjabe.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.conversation}")
public class ConversationDto {

    @Schema(description = "${docs.dto.conversation.id}", accessMode = READ_ONLY)
    private UUID id;

    @Schema(description = "${docs.dto.conversation.username}", accessMode = READ_ONLY)
    private String username;

    @Schema(description = "${docs.dto.conversation.messages}", accessMode = READ_ONLY)
    private List<MessageDto> messages;

    @Schema(description = "${docs.dto.conversation.summary}")
    private String summary;

    @Schema(description = "${docs.dto.conversation.createdAt}", accessMode = READ_ONLY)
    private String createdAt;
}
