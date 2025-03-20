package com.shiftsl.backend.Exceptions;

public class ShiftClaimFailedException extends RuntimeException {
    public ShiftClaimFailedException(String message) {
        super(message);
    }
}
