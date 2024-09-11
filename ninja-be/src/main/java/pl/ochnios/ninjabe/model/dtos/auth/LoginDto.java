package pl.ochnios.ninjabe.model.dtos.auth;

import lombok.Data;

@Data
public class LoginDto {

    private final String username;
    private final String password;
}
