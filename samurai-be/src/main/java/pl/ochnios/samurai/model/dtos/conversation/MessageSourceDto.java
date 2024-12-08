package pl.ochnios.samurai.model.dtos.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "${docs.dto.message.source}")
public class MessageSourceDto {

    @Schema(description = "${docs.dto.message.source.id}")
    private UUID id;

    @Schema(description = "${docs.dto.document.id}")
    private UUID documentId;

    @Schema(description = "${docs.dto.message.source.retrievedContent}")
    private String retrievedContent;

    @Schema(description = "${docs.dto.message.source.originalTitle}")
    private String originalTitle;

    @Schema(description = "${docs.dto.message.source.updated}")
    private boolean updated;

    @Schema(description = "${docs.dto.message.source.deleted}")
    private boolean deleted;
}
