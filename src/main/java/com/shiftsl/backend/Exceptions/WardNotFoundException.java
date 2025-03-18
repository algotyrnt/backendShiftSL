package com.shiftsl.backend.Exceptions;

public class WardNotFoundException extends RuntimeException {
  public WardNotFoundException(String message) {
    super(message);
  }
}
