package pl.ochnios.samurai.model.dtos.document.chunk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "${docs.dto.document.chunkCriteria}")
public class ChunkCriteria {

    @Schema(description = "${docs.dto.chunkCriteria.globalSearch}")
    private String globalSearch;

    private String content;
    private Integer minPosition;
    private Integer maxPosition;
    private Integer minLength;
    private Integer maxLength;
    private Instant minUpdatedAt;
    private Instant maxUpdatedAt;
}
