package pl.ochnios.samurai.services.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ochnios.samurai.commons.exceptions.ValidationException;
import pl.ochnios.samurai.model.dtos.auth.LoginDto;
import pl.ochnios.samurai.model.dtos.auth.RegisterDto;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.model.mappers.UserMapper;
import pl.ochnios.samurai.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto authenticate(LoginDto loginDto, HttpServletResponse response) {
        var authToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        var auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        var user = (User) auth.getPrincipal();
        var jwt = jwtService.generateJwt(user);
        jwtService.setJwtCookie(response, jwt);
        return userMapper.map(user);
    }

    public void unauthenticate(HttpServletResponse response) {
        jwtService.unsetJwtCookie(response);
    }

    public UserDto register(RegisterDto registerDto) {
        validateRegisterDto(registerDto);

        var user = User.builder()
                .username(registerDto.getUsername())
                .firstname(registerDto.getFirstname())
                .lastname(registerDto.getLastname())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .build();

        // TODO email verification

        user = userRepository.save(user);
        return userMapper.map(user);
    }

    public User getAuthenticatedUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername());
        } else {
            throw new AuthenticationServiceException("Bad principal type");
        }
    }

    private void validateRegisterDto(RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new ValidationException("Username already exists");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ValidationException("Email already exists");
        }
    }
}
