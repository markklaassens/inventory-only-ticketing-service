package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

public record BookingRequestDto(String passengerEmail, Map<String, List<SeatDto>> serviceSeatMap) {

  public BookingRequestDto {
    requireNonNull(passengerEmail, "Passenger email cannot be null");
    serviceSeatMap = Map.copyOf(requireNonNull(serviceSeatMap, "Service seat map cannot be null"));
  }
}
