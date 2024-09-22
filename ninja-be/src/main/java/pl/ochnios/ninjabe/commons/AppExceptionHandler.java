package pl.ochnios.ninjabe.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.ochnios.ninjabe.commons.exceptions.ApplicationException;
import pl.ochnios.ninjabe.commons.exceptions.JsonPatchException;
import pl.ochnios.ninjabe.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.ninjabe.commons.exceptions.ValidationException;
import pl.ochnios.ninjabe.model.dtos.AppError;

import java.util.ArrayList;
import java.util.List;
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

    @ExceptionHandler(JsonPatchException.class)
    public ResponseEntity<AppError> handleJsonPatchException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<AppError> handleValidationException(ValidationException ex) {
        final var errors = ex.getMessages();
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        final var errors = processFieldErrors(ex.getFieldErrors());
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppError> handleAuthenticationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<AppError> logAndGetResponse(Exception ex, HttpStatus status) {
        final var errors = List.of(ex.getMessage());
        return logAndGetResponse(ex, status, errors);
    }

    private ResponseEntity<AppError> logAndGetResponse(
            Exception ex, HttpStatus status, Iterable<String> errors) {
        final var errorId = UUID.randomUUID();
        if (status.is5xxServerError()) {
            log.error(String.format("errorId=%s, message=%s", errorId, errors), ex);
            final var body = AppError.create(errorId, "Unexpected error");
            return ResponseEntity.status(status).body(body);
        } else {
            log.warn(String.format("errorId=%s, message=%s", errorId, errors));
            final var body = AppError.create(errorId, errors);
            return ResponseEntity.status(status).body(body);
        }
    }

    private Iterable<String> processFieldErrors(List<FieldError> fieldErrors) {
        final var errors = new ArrayList<String>();
        for (var fieldError : fieldErrors) {
            errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        return errors;
    }
}
