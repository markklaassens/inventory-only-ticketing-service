package com.example.inventoryonlyticketing.exceptions;

public class StationNotFoundException extends RuntimeException {

  public StationNotFoundException(String msg) {
    super(msg);
  }
}
