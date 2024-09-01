package com.example.inventoryonlyticketing.exceptions;

public class UnavailableSeatsException extends RuntimeException {

  public UnavailableSeatsException(String msg) {
    super(msg);
  }
}
