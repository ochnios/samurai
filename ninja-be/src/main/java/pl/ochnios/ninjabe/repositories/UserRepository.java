package pl.ochnios.ninjabe.repositories;

import pl.ochnios.ninjabe.model.entities.user.User;

public interface UserRepository {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    User save(User user);
}
