package pl.ochnios.samurai.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static pl.ochnios.samurai.model.entities.conversation.Conversation.MAX_SUMMARY_LENGTH;
import static pl.ochnios.samurai.model.entities.conversation.Conversation.MIN_SUMMARY_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.conversationSummary}")
public class ConversationSummaryDto {

    @NotPatchable
    @Schema(description = "${docs.dto.conversation.id}", accessMode = READ_ONLY)
    private UUID id;

    @Schema(description = "${docs.dto.conversation.summary}", accessMode = READ_ONLY)
    @NotBlank(message = "must not be blank or null")
    @Size(min = MIN_SUMMARY_LENGTH, message = "must have at least " + MIN_SUMMARY_LENGTH + " characters")
    @Size(max = MAX_SUMMARY_LENGTH, message = "must have at most " + MAX_SUMMARY_LENGTH + " characters")
    private String summary;

    @NotPatchable
    @Schema(description = "${docs.dto.conversation.createdAt}", accessMode = READ_ONLY)
    private String createdAt;
}
