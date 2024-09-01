package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import java.util.List;

public record BookingDto(String bookingId, String passengerEmail, List<TicketDto> tickets) {

  public BookingDto {
    requireNonNull(bookingId, "Booking Id cannot be null");
    requireNonNull(passengerEmail, "Passenger Email cannot be null");
    tickets = List.copyOf(requireNonNull(tickets, "Tickets cannot be null"));
  }
}
