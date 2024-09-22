package pl.ochnios.ninjabe.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.ninjabe.model.dtos.auth.LoginDto;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;
import pl.ochnios.ninjabe.services.security.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @RequestBody LoginDto loginDto, HttpServletResponse response) {
        final var user = authService.authenticate(loginDto, response);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.unauthenticate(response);
        return ResponseEntity.ok().build();
    }
}
