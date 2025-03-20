package com.shiftsl.backend.Exceptions;

public class ShiftsNotFoundException extends RuntimeException {
    public ShiftsNotFoundException(String message) {
        super(message);
    }
}
