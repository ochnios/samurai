package pl.ochnios.samurai.controllers;

import static pl.ochnios.samurai.commons.AppConstants.HTTP_200;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_400;
import static pl.ochnios.samurai.commons.AppConstants.HTTP_404;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.json.JsonPatch;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.dtos.user.UserCriteria;
import pl.ochnios.samurai.model.dtos.user.UserDto;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "${docs.ctrl.user.tag.name}", description = "${docs.ctrl.user.tag.desc}")
public interface UserApi {

    @Operation(summary = "${docs.ctrl.user.searchUsers}", description = "${docs.ctrl.user.searchUsers.desc}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    ResponseEntity<PageDto<UserDto>> searchUsers(
            @ParameterObject UserCriteria criteria, @ParameterObject PageRequestDto pageRequestDto);

    @Operation(summary = "${docs.ctrl.user.get}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<UserDto> getUser(@PathVariable String username);

    @Operation(summary = "${docs.ctrl.user.patch}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_404)
    ResponseEntity<UserDto> patchUser(@PathVariable String username, @RequestBody JsonPatch jsonPatch);
}
