package pl.ochnios.samurai.model.dtos.document;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.dtos.file.FileDto;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.document}")
public class DocumentDto extends FileDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.document.user}")
    private UserDto user;

    @Schema(description = "${docs.dto.document.title}")
    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 255, message = "must have at most 255 characters")
    private String title;

    @Schema(description = "${docs.dto.document.description}")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 2048, message = "must have at most 2048 characters")
    private String description;

    @NotNull(message = "must not be null")
    @Schema(description = "${docs.dto.document.status}")
    private DocumentStatus status;

    @NotPatchable
    @Schema(description = "${docs.dto.document.createdAt}", accessMode = READ_ONLY)
    private String createdAt;
}
