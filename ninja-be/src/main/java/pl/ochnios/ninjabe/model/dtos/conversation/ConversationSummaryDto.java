package pl.ochnios.ninjabe.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.conversationSummary}")
public class ConversationSummaryDto {

    @Schema(description = "${docs.dto.conversationSummary.id}", accessMode = READ_ONLY)
    private UUID id;

    @Schema(description = "${docs.dto.conversationSummary.summary}", accessMode = READ_ONLY)
    private String summary;

    @Schema(description = "${docs.dto.conversationSummary.createdAt}", accessMode = READ_ONLY)
    private Instant createdAt;
}
