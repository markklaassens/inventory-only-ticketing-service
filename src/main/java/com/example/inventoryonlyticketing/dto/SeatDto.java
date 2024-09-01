package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

public record SeatDto(String carriageId, int seatNumber) {
  public SeatDto {
    requireNonNull(carriageId, "Carriage id cannot be null");
  }

  public static SeatDto fromString(String seat) {
    requireNonNull(seat, "Seat cannot be null");
    final var carriageId = seat.substring(0, 1);
    final var seatNumber = Integer.parseInt(seat.substring(1));
    return new SeatDto(carriageId, seatNumber);
  }
}
