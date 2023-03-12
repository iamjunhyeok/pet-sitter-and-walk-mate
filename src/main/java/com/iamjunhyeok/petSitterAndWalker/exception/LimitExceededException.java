package com.iamjunhyeok.petSitterAndWalker.exception;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String message) {
        super(message);
    }
}
