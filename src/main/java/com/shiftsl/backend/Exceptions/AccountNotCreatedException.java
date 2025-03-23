package com.shiftsl.backend.Exceptions;

public class AccountNotCreatedException extends RuntimeException {
  public AccountNotCreatedException(String message) {
    super(message);
  }
}
