package com.iamjunhyeok.petSitterAndWalker.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
