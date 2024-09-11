package pl.ochnios.ninjabe.model.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.UserRepository;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements DataSeeder {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void seed() {
        if (!userRepository.existsByUsername("user")) {
            final var user =
                    User.builder()
                            .username("user")
                            .email("user@users.com")
                            .password(passwordEncoder.encode("user"))
                            .conversations(new ArrayList<>())
                            .build();
            final var savedUser = userRepository.save(user);
            log.info("Created user: {}", savedUser);
        }
    }
}
