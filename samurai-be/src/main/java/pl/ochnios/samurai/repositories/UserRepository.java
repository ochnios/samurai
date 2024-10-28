package pl.ochnios.samurai.repositories;

import pl.ochnios.samurai.model.entities.user.User;

public interface UserRepository {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    User save(User user);
}
