package pl.ochnios.ninjabe.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import pl.ochnios.ninjabe.exceptions.ApplicationException;
import pl.ochnios.ninjabe.exceptions.ResourceNotFoundException;
import pl.ochnios.ninjabe.model.dtos.ApiError;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiError> handleException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationException.class)
    public final ResponseEntity<ApiError> handleApplicationException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ApiError> handleResourceNotFoundException(Exception ex) {
        return logAndGetResponse(ex, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiError> logAndGetResponse(Exception ex, HttpStatus status) {
        var errorId = UUID.randomUUID();
        if (status.is5xxServerError()) {
            log.error(String.format("errorId=%s, message=%s", errorId, ex.getMessage()), ex);
            var body = ApiError.create(errorId, "Unexpected error");
            return ResponseEntity.status(status).body(body);
        } else {
            log.warn(String.format("errorId=%s, message=%s", errorId, ex.getMessage()));
            var body = ApiError.create(errorId, ex.getMessage());
            return ResponseEntity.status(status).body(body);
        }
    }
}
