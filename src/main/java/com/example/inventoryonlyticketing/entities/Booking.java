package com.example.inventoryonlyticketing.entities;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

import java.util.Set;
import java.util.UUID;

public class Booking {

  private final UUID bookingId;
  private final Passenger passenger;
  private final Set<Ticket> tickets;

  public Booking(Set<Ticket> ticketList, Passenger passenger) {
    this.bookingId = randomUUID();
    this.passenger = requireNonNull(passenger, "Passenger cannot be null");
    this.tickets = requireNonNull(ticketList, "Ticket list cannot be null");
  }

  public UUID getBookingId() {
    return bookingId;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public Set<Ticket> getTickets() {
    return tickets;
  }
}
