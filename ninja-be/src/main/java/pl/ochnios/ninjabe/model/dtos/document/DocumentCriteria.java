package pl.ochnios.ninjabe.model.dtos.document;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import pl.ochnios.ninjabe.model.entities.document.DocumentStatus;

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

    private Instant minCreatedAt;
    private Instant maxCreatedAt;
    private DocumentStatus status;
}
