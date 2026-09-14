package com.gustavomp.devtrackerapi.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return buildResponseEntity(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            String acceptedValues = String.join(", ",
                    Arrays.stream(requiredType.getEnumConstants())
                            .map(Object::toString)
                            .toList());

            errors.put(ex.getName(), String.format(
                    "Invalid value '%s'. Accepted values are: %s",
                    ex.getValue(),
                    acceptedValues
            ));
        } else {
            errors.put(ex.getName(), String.format("Invalid value '%s'", ex.getValue()));
        }

        return buildResponseEntity(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().isEmpty()
                    ? "field"
                    : ife.getPath().getLast().getPropertyName();

            Object invalidValue = ife.getValue();

            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String acceptedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");

                fieldErrors.put(fieldName, String.format(
                                "Invalid value '%s'. Accepted values are: %s",
                                invalidValue,
                                acceptedValues
                        )
                );
            } else {
                fieldErrors.put(fieldName, String.format("Invalid value '%s'", invalidValue));
            }
        } else {
            fieldErrors.put("request", "Invalid JSON input format");
        }

        return buildResponseEntity(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEntityAlreadyExists(EntityAlreadyExistsException ex) {
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
//        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, Object errorContent) {
        Map<String, Object> response = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", errorContent
        );

        return new ResponseEntity<>(response, status);
    }

}