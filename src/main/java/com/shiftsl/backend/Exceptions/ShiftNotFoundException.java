package com.shiftsl.backend.Exceptions;

public class ShiftNotFoundException extends RuntimeException {
    public ShiftNotFoundException(String message) {
        super(message);
    }
}
