package pl.ochnios.ninjabe.model.dtos.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.ochnios.ninjabe.commons.patch.NotPatchable;
import pl.ochnios.ninjabe.model.dtos.PatchDto;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;
import pl.ochnios.ninjabe.model.entities.document.DocumentStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.document}")
public class DocumentDto extends FileDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.document.uploader}")
    private UserDto uploader;

    @Schema(description = "${docs.dto.document.title}")
    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 255, message = "must have at most 255 characters")
    private String title;

    @Schema(description = "${docs.dto.document.description}")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 2048, message = "must have at most 2048 characters")
    private String description;

    @Schema(description = "${docs.dto.document.status}")
    private DocumentStatus status;
}
