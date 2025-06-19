package com.shiftsl.backend.Exceptions;

public class LeaveNotSavedException extends RuntimeException{

    public LeaveNotSavedException(String message) {
        super(message);
    }
}
