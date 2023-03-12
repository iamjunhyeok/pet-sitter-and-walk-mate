package com.iamjunhyeok.petSitterAndWalker.exception;

public class SendVerificationCodeException extends RuntimeException {
    public SendVerificationCodeException() {
        super();
    }

    public SendVerificationCodeException(String message) {
        super(message);
    }
}
