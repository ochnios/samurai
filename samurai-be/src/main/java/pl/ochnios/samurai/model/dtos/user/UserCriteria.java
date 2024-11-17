package pl.ochnios.samurai.model.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import pl.ochnios.samurai.model.entities.user.Role;

@Data
@Builder
@Schema(description = "${docs.dto.userCriteria}")
public class UserCriteria {

    @Schema(description = "${docs.dto.userCriteria.globalSearch}")
    private String globalSearch;

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private Instant minCreatedAt;
    private Instant maxCreatedAt;
}
