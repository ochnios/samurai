package pl.ochnios.samurai.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.ochnios.samurai.model.entities.user.User;

public interface UserRepository {

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    User save(User user);
}
