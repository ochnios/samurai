package pl.ochnios.ninjabe.controllers;

import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_200;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_400;
import static pl.ochnios.ninjabe.commons.AppConstants.HTTP_401;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ochnios.ninjabe.model.dtos.auth.LoginDto;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;

@Tag(name = "${docs.ctrl.auth.tag.name}", description = "${docs.ctrl.auth.tag.desc}")
public interface AuthApi {

    @Operation(summary = "${docs.ctrl.auth.login}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_400)
    @ApiResponse(responseCode = HTTP_401)
    ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "${docs.ctrl.auth.logout}")
    @ApiResponse(responseCode = HTTP_200)
    @ApiResponse(responseCode = HTTP_401)
    ResponseEntity<Void> logout(HttpServletResponse response);
}
