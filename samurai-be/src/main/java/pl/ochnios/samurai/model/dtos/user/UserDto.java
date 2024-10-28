package pl.ochnios.samurai.model.dtos.user;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ochnios.samurai.model.entities.user.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "${docs.dto.user}")
public class UserDto {

    @Schema(description = "${docs.dto.user.username}", accessMode = AccessMode.READ_ONLY)
    private String username;

    @Schema(description = "${docs.dto.user.firstname}")
    private String firstname;

    @Schema(description = "${docs.dto.user.lastname}")
    private String lastname;

    @Schema(description = "${docs.dto.user.email}", accessMode = READ_ONLY)
    private String email;

    @Schema(description = "${docs.dto.user.role}")
    private Role role;
}
