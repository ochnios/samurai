package pl.ochnios.ninjabe.model.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.UserRepository;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements DataSeeder {

    private final UserRepository userRepository;

    @Override
    public void seed() {
        if (!userRepository.existsByUsername("user")) {
            final var user = new User("user", new ArrayList<>());
            final var savedUser = userRepository.save(user);
            log.info("Created user: {}", savedUser);
        }
    }
}
