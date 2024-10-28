package pl.ochnios.samurai.model.dtos.document.chunk;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;
import pl.ochnios.samurai.model.dtos.PatchDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.document.chunk}")
public class DocumentChunkDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.id}")
    private UUID id;

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.position}")
    private int position;

    @Schema(description = "${docs.dto.document.chunk.content}")
    @NotBlank(message = "must not be blank or null")
    @Size(min = 20, message = "must have at least 50 characters")
    @Size(max = 8192, message = "must have at most 8192 characters")
    private String content;

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.updatedAt}", accessMode = READ_ONLY)
    private String updatedAt;
}
