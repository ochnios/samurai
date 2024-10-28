package pl.ochnios.samurai.controllers.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.controllers.AuthApi;
import pl.ochnios.samurai.model.dtos.auth.LoginDto;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.services.security.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        final var user = authService.authenticate(loginDto, response);
        return ResponseEntity.ok(user);
    }

    @Override
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.unauthenticate(response);
        return ResponseEntity.ok().build();
    }
}
