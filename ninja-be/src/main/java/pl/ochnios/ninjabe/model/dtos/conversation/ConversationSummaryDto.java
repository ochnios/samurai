package pl.ochnios.ninjabe.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.ninjabe.commons.patch.NotPatchable;

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
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 32, message = "must have at most 32 characters")
    private String summary;

    @NotPatchable
    @Schema(description = "${docs.dto.conversation.createdAt}", accessMode = READ_ONLY)
    private String createdAt;
}
