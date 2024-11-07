package pl.ochnios.samurai.model.dtos.document.chunk;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;
import pl.ochnios.samurai.model.dtos.PatchDto;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "${docs.dto.document.chunk}")
public class ChunkDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.id}", accessMode = READ_ONLY)
    private UUID id;

    @NotPatchable
    @Schema(description = "${docs.dto.document.id}", accessMode = READ_ONLY)
    private UUID documentId;

    @Schema(description = "${docs.dto.document.chunk.position}")
    @PositiveOrZero
    private int position;

    @Schema(description = "${docs.dto.document.chunk.content}")
    @NotBlank(message = "must not be blank or null")
    @Size(min = 20, message = "must have at least 20 characters")
    @Size(max = 8192, message = "must have at most 8192 characters")
    private String content;

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.length}", accessMode = READ_ONLY)
    private int length;

    @NotPatchable
    @Schema(description = "${docs.dto.document.chunk.updatedAt}", accessMode = READ_ONLY)
    private String updatedAt;
}
