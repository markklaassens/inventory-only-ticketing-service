package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

public record PassengerDto(String passengerEmail, String passengerName) {

  public PassengerDto {
    requireNonNull(passengerEmail, "Passenger email cannot be null");
    requireNonNull(passengerName, "Passenger name cannot be null");
  }
}
