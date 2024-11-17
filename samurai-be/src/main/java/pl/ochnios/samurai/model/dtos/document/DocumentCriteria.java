package pl.ochnios.samurai.model.dtos.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

import java.time.Instant;

@Data
@Builder
@Schema(description = "${docs.dto.documentCriteria}")
public class DocumentCriteria {

    @Schema(description = "${docs.dto.documentCriteria.globalSearch}")
    private String globalSearch;

    private String title;
    private String description;
    private String userFullName;

    private String filename;
    private Long minSize;
    private Long maxSize;

    private Instant minUpdatedAt;
    private Instant maxUpdatedAt;
    private DocumentStatus status;
}
