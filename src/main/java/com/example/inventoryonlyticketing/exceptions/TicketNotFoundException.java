package com.example.inventoryonlyticketing.exceptions;

public class TicketNotFoundException extends RuntimeException {

  public TicketNotFoundException(String msg) {
    super(msg);
  }
}
