package com.iamjunhyeok.petSitterAndWalker.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Void> invalidVerificationCodeException(InvalidVerificationCodeException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Void> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return buildErrorResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Void> handleTooManyRequestsException(TooManyRequestsException e) {
        return buildErrorResponse(e, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> messages = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return new ResponseEntity<>(new ErrorResponse(messages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<Void> handlePasswordMismatchException(PasswordMismatchException e) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Void> buildErrorResponse(Exception e, HttpStatus status) {
        log.error(e.getMessage());
        return new ResponseEntity<>(status);
    }

    @Getter
    class ErrorResponse {
        private LocalDateTime timestamp;
        private List<String> messages;

        public ErrorResponse(List<String> messages) {
            this.timestamp = LocalDateTime.now();
            this.messages = messages;
        }
    }
}
