package com.shiftsl.backend.Exceptions;

public class LeaveNotFoundException extends RuntimeException {
  public LeaveNotFoundException(String message) {
    super(message);
  }
}
