package pl.ochnios.ninjabe.model.seeders;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.model.entities.user.Role;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements DataSeeder {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void seed() {
        createUser("user", "John", "User", Role.User);
        createUser("mod", "John", "Mod", Role.Mod);
        createUser("admin", "John", "Admin", Role.Admin);
    }

    private void createUser(String username, String firstname, String lastname, Role role) {
        if (!userRepository.existsByUsername(username)) {
            final var user = User.builder()
                    .username(username)
                    .firstname(firstname)
                    .lastname(lastname)
                    .email(username + "@users.com")
                    .password(passwordEncoder.encode(username))
                    .role(role)
                    .conversations(new ArrayList<>())
                    .build();
            final var savedUser = userRepository.save(user);
            log.info("Created user: {}", savedUser);
        }
    }
}
