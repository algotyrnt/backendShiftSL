package com.shiftsl.backend.Exceptions;

public class PhoneNotInUseException extends RuntimeException {
    public PhoneNotInUseException(String message) {
        super(message);
    }
}
