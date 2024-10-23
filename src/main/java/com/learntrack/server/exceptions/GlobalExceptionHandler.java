package com.learntrack.server.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        logger.info("Resource not found error: {}", errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex.getRequiredType() == null) {
            String errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type is '%s'.",
                    ex.getValue(), ex.getName(), "unknown");
            errors.put(ex.getName(), errorMessage);
        } else {
            String errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type is '%s'.",
                    ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
            errors.put(ex.getName(), errorMessage);
        }

        logger.info("Type mismatch error: {}", errors);

        return ResponseEntity.badRequest().body(errors);
    }

    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        logger.info("Validation error: {}", errors);

        return ResponseEntity.badRequest().body(errors);
    }
}
