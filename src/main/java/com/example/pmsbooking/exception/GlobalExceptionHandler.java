package com.example.pmsbooking.exception;

import com.example.pmsbooking.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleBookingConflict(
            BookingConflictException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                409,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                403,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(403).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "Validation failed",
                LocalDateTime.now()
        );

        return ResponseEntity.status(400).body(errorResponse);
    }
}
