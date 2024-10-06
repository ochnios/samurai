package pl.ochnios.ninjabe.model.dtos.conversation;

import java.time.Instant;
import lombok.Data;

@Data
public class ConversationCriteria {

    private String globalSearch;
    private Integer minMessageCount;
    private Integer maxMessageCount;
    private Instant minCreatedAt;
    private Instant maxCreatedAt;
    private String summary;
    private String userFirstname;
    private String userLastname;
}
