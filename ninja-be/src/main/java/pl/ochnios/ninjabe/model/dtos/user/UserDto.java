package pl.ochnios.ninjabe.model.dtos.user;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Data;
import pl.ochnios.ninjabe.model.entities.user.Role;

@Data
@Schema(name = "UserDto", description = "${docs.dto.user}")
public class UserDto {

    @Schema(description = "${docs.dto.user.username}", accessMode = AccessMode.READ_ONLY)
    private final String username;

    @Schema(description = "${docs.dto.user.email}", accessMode = READ_ONLY)
    private final String email;

    @Schema(description = "${docs.dto.user.role}")
    private final Role role;
}
