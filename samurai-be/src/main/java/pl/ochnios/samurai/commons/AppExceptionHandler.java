package pl.ochnios.samurai.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.commons.exceptions.JsonPatchException;
import pl.ochnios.samurai.commons.exceptions.ResourceNotFoundException;
import pl.ochnios.samurai.commons.exceptions.ValidationException;
import pl.ochnios.samurai.model.dtos.AppError;

@Slf4j
@RestControllerAdvice
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
        var errors = ex.getMessages();
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = processFieldErrors(ex.getFieldErrors());
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<AppError> handleAuthorizationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppError> handleAuthenticationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<AppError> logAndGetResponse(Exception ex, HttpStatus status) {
        if (ex.getMessage() == null) {
            return logAndGetResponse(ex, status, List.of());
        } else {
            return logAndGetResponse(ex, status, List.of(ex.getMessage()));
        }
    }

    private ResponseEntity<AppError> logAndGetResponse(Exception ex, HttpStatus status, Iterable<String> errors) {
        var errorId = UUID.randomUUID();
        if (status.is5xxServerError()) {
            log.error(String.format("errorId=%s, message=%s", errorId, errors), ex);
            var body = AppError.create(errorId, "Unexpected error");
            return ResponseEntity.status(status).body(body);
        } else {
            log.warn(String.format("errorId=%s, message=%s", errorId, errors));
            var body = AppError.create(errorId, errors);
            return ResponseEntity.status(status).body(body);
        }
    }

    private Iterable<String> processFieldErrors(List<FieldError> fieldErrors) {
        var errors = new ArrayList<String>();
        for (var fieldError : fieldErrors) {
            errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        return errors;
    }
}
