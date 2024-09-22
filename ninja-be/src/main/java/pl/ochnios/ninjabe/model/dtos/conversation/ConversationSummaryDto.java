package pl.ochnios.ninjabe.model.dtos.conversation;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversationSummaryDto {

    private UUID id;
    private String summary;
    private Instant createdAt;
}
