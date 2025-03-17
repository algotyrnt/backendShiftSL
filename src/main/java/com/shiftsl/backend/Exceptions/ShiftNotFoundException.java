package com.shiftsl.backend.Exceptions;

public class ShiftNotFoundException extends RuntimeException {
    public ShiftNotFoundException(Long shiftID) {
        super("Shift - " + shiftID + "not found.");
    }
}
