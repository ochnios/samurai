package pl.ochnios.samurai.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.model.entities.user.User;
import pl.ochnios.samurai.repositories.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserCrudRepository userCrudRepository;

    @Override
    public Page<User> findAll(Specification<User> specification, Pageable pageable) {
        return userCrudRepository.findAll(specification, pageable);
    }

    @Override
    public User findByUsername(String username) {
        return userCrudRepository
                .findByUsername(username)
                .orElseThrow(() -> ResourceNotFoundException.of(User.class, username));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userCrudRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return userCrudRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userCrudRepository.existsByEmail(email);
    }
}
