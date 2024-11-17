package pl.ochnios.samurai.model.dtos.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.document.content}")
public class DocumentContentDto {

    @Schema(description = "${docs.dto.document.id}", accessMode = READ_ONLY)
    private UUID id;

    @Schema(description = "${docs.dto.document.content}", accessMode = READ_ONLY)
    private String content;
}
