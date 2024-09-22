package pl.ochnios.ninjabe.repositories.impl;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import pl.ochnios.ninjabe.model.entities.user.User;

public interface UserCrudRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
