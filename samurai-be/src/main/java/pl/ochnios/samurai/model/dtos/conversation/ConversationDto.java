package pl.ochnios.samurai.model.dtos.conversation;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;
import pl.ochnios.samurai.model.dtos.PatchDto;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.conversation}")
public class ConversationDto extends ConversationSummaryDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.conversation.messages}", accessMode = READ_ONLY)
    private List<MessageDto> messages;
}
