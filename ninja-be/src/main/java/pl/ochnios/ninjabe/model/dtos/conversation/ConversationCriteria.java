package pl.ochnios.ninjabe.model.dtos.conversation;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationCriteria {

    private String globalSearch;
    private Integer minMessageCount;
    private Integer maxMessageCount;
    private Instant minCreatedAt;
    private Instant maxCreatedAt;
    private String summary;
    private String userFirstname;
    private String userLastname;
    private Boolean deleted;
}
