package pl.ochnios.ninjabe.model.dtos.document;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.ninjabe.commons.patch.NotPatchable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.file}")
public abstract class FileDto {

    @NotPatchable
    @Schema(description = "${docs.dto.file.id}")
    private UUID id;

    @NotPatchable
    @Schema(description = "${docs.dto.file.name}")
    private String name;

    @NotPatchable
    @Schema(description = "${docs.dto.file.mimeType}")
    private String mimeType;

    @NotPatchable
    @Schema(description = "${docs.dto.size}")
    private long size;
}
