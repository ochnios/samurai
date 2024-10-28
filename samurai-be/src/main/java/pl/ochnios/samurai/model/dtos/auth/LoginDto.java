package pl.ochnios.samurai.model.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "${docs.dto.login}")
public class LoginDto {

    @Schema(description = "${docs.dto.login.username}", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String username;

    @Schema(description = "${docs.dto.login.password}", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String password;
}
