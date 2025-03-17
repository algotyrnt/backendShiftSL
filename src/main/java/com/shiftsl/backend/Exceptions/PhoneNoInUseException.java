package com.shiftsl.backend.Exceptions;

public class PhoneNoInUseException extends RuntimeException {
    public PhoneNoInUseException(String message) {
        super(message);
    }
}
