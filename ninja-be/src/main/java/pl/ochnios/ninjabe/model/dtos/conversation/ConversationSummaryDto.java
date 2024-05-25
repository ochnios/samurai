package pl.ochnios.ninjabe.model.dtos.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ConversationSummaryDto {

    private UUID conversationId;
    private String summary;
}
