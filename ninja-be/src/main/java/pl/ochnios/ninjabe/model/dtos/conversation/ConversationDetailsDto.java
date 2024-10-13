package pl.ochnios.ninjabe.model.dtos.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "${docs.dto.conversationDetails}")
public class ConversationDetailsDto extends ConversationSummaryDto {

    @Schema(description = "${docs.dto.conversation.user}")
    private UserDto user;

    @Schema(description = "${docs.dto.conversation.messageCount}")
    private int messageCount;

    @Schema(description = "${docs.dto.conversation.deleted}")
    private boolean deleted;
}
