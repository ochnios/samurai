package pl.ochnios.samurai.model.dtos.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "${docs.dto.conversationCriteria}")
public class ConversationCriteria {

    @Schema(description = "${docs.dto.conversationCriteria.globalSearch}")
    private String globalSearch;

    private Integer minMessageCount;
    private Integer maxMessageCount;
    private Instant minCreatedAt;
    private Instant maxCreatedAt;
    private String summary;
    private String userFullName;
    private Boolean deleted;
}
