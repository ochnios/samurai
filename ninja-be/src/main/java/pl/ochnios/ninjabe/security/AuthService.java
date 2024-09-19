package pl.ochnios.ninjabe.security;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.dtos.auth.LoginDto;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.model.mappers.UserMapper;
import pl.ochnios.ninjabe.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserDto authenticate(LoginDto loginDto, HttpServletResponse response) {
        final var authToken =
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(), loginDto.getPassword());
        final var auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        final var user = (User) auth.getPrincipal();
        final var jwt = jwtService.generateJwt(user);
        jwtService.setJwtCookie(response, jwt);
        return userMapper.map(user);
    }

    public void unauthenticate(HttpServletResponse response) {
        jwtService.unsetJwtCookie(response);
    }

    public User getAuthenticatedUser() {
        final var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername());
        } else {
            throw new AuthenticationServiceException("Bad principal type");
        }
    }
}
