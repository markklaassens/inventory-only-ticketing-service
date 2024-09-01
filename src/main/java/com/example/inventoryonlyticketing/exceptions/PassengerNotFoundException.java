package com.example.inventoryonlyticketing.exceptions;

public class PassengerNotFoundException extends RuntimeException {

  public PassengerNotFoundException(String msg) {
    super(msg);
  }
}
