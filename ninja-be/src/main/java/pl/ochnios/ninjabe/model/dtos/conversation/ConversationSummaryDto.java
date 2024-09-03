package pl.ochnios.ninjabe.model.dtos.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ConversationSummaryDto {

    private UUID id;
    private String summary;
    private Instant createdAt;
}
