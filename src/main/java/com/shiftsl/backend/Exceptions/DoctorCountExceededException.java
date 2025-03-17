package com.shiftsl.backend.Exceptions;

public class DoctorCountExceededException extends RuntimeException {
    public DoctorCountExceededException(String message) {
        super(message);
    }
}
