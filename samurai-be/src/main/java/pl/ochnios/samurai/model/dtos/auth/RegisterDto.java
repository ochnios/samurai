package pl.ochnios.samurai.model.dtos.auth;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static pl.ochnios.samurai.model.entities.user.User.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "${docs.dto.register}")
public class RegisterDto {

    @Schema(description = "${docs.dto.register.username}", requiredMode = REQUIRED)
    @NotBlank(message = "must not be blank or null")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]{3,30}$",
            message = "must be between 3 and 30 characters and can only "
                    + "contain letters, numbers, dots, underscores, and hyphens.")
    private final String username;

    @Schema(description = "${docs.dto.register.firstname}", requiredMode = REQUIRED)
    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = MAX_NAME_LENGTH, message = "must have at most " + MAX_NAME_LENGTH + " characters")
    private final String firstname;

    @Schema(description = "${docs.dto.register.lastname}", requiredMode = REQUIRED)
    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = MAX_NAME_LENGTH, message = "must have at most " + MAX_NAME_LENGTH + " characters")
    private final String lastname;

    @Schema(description = "${docs.dto.register.email}", requiredMode = REQUIRED)
    @NotBlank(message = "must not be blank or null")
    @Email(message = "must be a correct email")
    private final String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#()_])[A-Za-z\\d@$!%*?&^#()_]{8,30}$",
            message = "must be 8-30 characters long, include at least one uppercase letter, "
                    + "one lowercase letter, one number, and one special character.")
    @Schema(description = "${docs.dto.register.password}", requiredMode = REQUIRED)
    private final String password;

    @Schema(description = "${docs.dto.register.confirmPassword}", requiredMode = REQUIRED)
    private final String confirmPassword;
}
