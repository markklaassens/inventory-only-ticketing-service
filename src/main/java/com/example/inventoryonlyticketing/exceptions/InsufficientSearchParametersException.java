package com.example.inventoryonlyticketing.exceptions;

public class InsufficientSearchParametersException extends RuntimeException {

  public InsufficientSearchParametersException(String msg) {
    super(msg);
  }
}
