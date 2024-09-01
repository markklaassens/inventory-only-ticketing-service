package com.example.inventoryonlyticketing.exceptions;

public class ServiceNotFoundException extends RuntimeException {

  public ServiceNotFoundException(String msg) {
    super(msg);
  }
}
