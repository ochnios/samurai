package pl.ochnios.samurai.services;

import javax.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.commons.patch.JsonPatchService;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.dtos.user.UserCriteria;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.model.entities.user.UserSpecification;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.model.mappers.UserMapper;
import pl.ochnios.samurai.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JsonPatchService patchService;
    private final PageMapper pageMapper;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userRepository.findByUsername(username);
        } catch (ResourceNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageDto<UserDto> searchUsers(UserCriteria criteria, PageRequestDto pageRequestDto) {
        var pageRequest = pageMapper.map(pageRequestDto);
        var specification = UserSpecification.create(criteria);
        var usersPage = userRepository.findAll(specification, pageRequest);
        return pageMapper.map(usersPage, userMapper::map);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(String username) {
        var user = userRepository.findByUsername(username);
        return userMapper.map(user);
    }

    @Transactional
    public UserDto patchUser(String username, JsonPatch jsonPatch) {
        var user = userRepository.findByUsername(username);
        patchService.apply(user, jsonPatch);
        var updatedUser = userRepository.save(user);
        return userMapper.map(updatedUser);
    }
}
