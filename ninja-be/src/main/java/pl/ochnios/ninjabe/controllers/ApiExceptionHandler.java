package pl.ochnios.ninjabe.controllers;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import pl.ochnios.ninjabe.model.dtos.ApiError;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ApiError> handleEntityNotFoundException(Exception ex) {
        var errorId = UUID.randomUUID();
        log.warn(String.format("errorId=%s, message=%s", errorId, ex.getMessage()));
        var body = ApiError.create(errorId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiError> handleException(Exception ex) {
        var errorId = UUID.randomUUID();
        log.error(String.format("errorId=%s, message=%s", errorId, ex.getMessage()), ex);
        var body = ApiError.create(errorId, "Unexpected error");
        return ResponseEntity.internalServerError().body(body);
    }
}
