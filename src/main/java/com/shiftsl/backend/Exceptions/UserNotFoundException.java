package com.shiftsl.backend.Exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User - " + userId + "not found.");
    }
}
