package pl.ochnios.ninjabe.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        // TODO configure custom user detail service
        return userRepository.findByUsername("user");
    }
}
