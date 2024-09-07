package pl.ochnios.ninjabe.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import pl.ochnios.ninjabe.exceptions.ApplicationException;
import pl.ochnios.ninjabe.exceptions.ResourceNotFoundException;
import pl.ochnios.ninjabe.model.dtos.AppError;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppError> handleException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<AppError> handleApplicationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AppError> handleResourceNotFoundException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppError> handleAuthenticationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<AppError> logAndGetResponse(Exception ex, HttpStatus status) {
        final var errorId = UUID.randomUUID();
        if (status.is5xxServerError()) {
            log.error(String.format("errorId=%s, message=%s", errorId, ex.getMessage()), ex);
            final var body = AppError.create(errorId, "Unexpected error");
            return ResponseEntity.status(status).body(body);
        } else {
            log.warn(String.format("errorId=%s, message=%s", errorId, ex.getMessage()));
            final var body = AppError.create(errorId, ex.getMessage());
            return ResponseEntity.status(status).body(body);
        }
    }
}
