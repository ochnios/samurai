package pl.ochnios.ninjabe.model.dtos.user;

import lombok.Data;
import pl.ochnios.ninjabe.model.entities.user.Role;

@Data
public class UserDto {

    private final String username;
    private final String email;
    private final Role role;
}
