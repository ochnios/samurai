package pl.ochnios.samurai.model.dtos.user;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.commons.patch.NotPatchable;
import pl.ochnios.samurai.model.dtos.PatchDto;
import pl.ochnios.samurai.model.entities.user.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.user}")
public class UserDto implements PatchDto {

    @NotPatchable
    @Schema(description = "${docs.dto.user.username}", accessMode = AccessMode.READ_ONLY)
    private String username;

    @NotPatchable
    @Schema(description = "${docs.dto.user.firstname}", accessMode = READ_ONLY)
    private String firstname;

    @NotPatchable
    @Schema(description = "${docs.dto.user.lastname}", accessMode = READ_ONLY)
    private String lastname;

    @NotPatchable
    @Schema(description = "${docs.dto.user.email}", accessMode = READ_ONLY)
    private String email;

    @Schema(description = "${docs.dto.user.role}")
    private Role role;

    @NotPatchable
    @Schema(description = "${docs.dto.user.createdAt}", accessMode = READ_ONLY)
    private String createdAt;
}
