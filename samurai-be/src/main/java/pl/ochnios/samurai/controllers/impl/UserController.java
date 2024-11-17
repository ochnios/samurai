package pl.ochnios.samurai.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ochnios.samurai.controllers.UserApi;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.dtos.user.UserCriteria;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.services.UserService;

import javax.json.JsonPatch;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageDto<UserDto>> searchUsers(
            UserCriteria criteria, PageRequestDto pageRequestDto) {
        var usersPage = userService.searchUsers(criteria, pageRequestDto);
        return ResponseEntity.ok(usersPage);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        var userDto = userService.getUser(username);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{username}", consumes = "application/json-patch+json")
    public ResponseEntity<UserDto> patchUser(
            @PathVariable String username, @RequestBody JsonPatch jsonPatch) {
        var patchedUser = userService.patchUser(username, jsonPatch);
        return ResponseEntity.ok(patchedUser);
    }
}
