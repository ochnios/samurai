package pl.ochnios.samurai.model.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "${docs.dto.login}")
public class LoginDto {

    @Schema(description = "${docs.dto.login.username}", requiredMode = REQUIRED)
    private final String username;

    @Schema(description = "${docs.dto.login.password}", requiredMode = REQUIRED)
    private final String password;
}
