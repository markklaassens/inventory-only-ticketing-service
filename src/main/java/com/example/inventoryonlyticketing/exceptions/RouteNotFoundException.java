package com.example.inventoryonlyticketing.exceptions;

public class RouteNotFoundException extends RuntimeException {

  public RouteNotFoundException(String msg) {
    super(msg);
  }
}
