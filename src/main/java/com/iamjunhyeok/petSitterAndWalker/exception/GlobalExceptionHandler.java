package com.iamjunhyeok.petSitterAndWalker.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    private ResponseEntity<Void> buildErrorResponse(Exception e, HttpStatus status) {
        log.error(e.getMessage());
        return new ResponseEntity<>(status);
    }
}
