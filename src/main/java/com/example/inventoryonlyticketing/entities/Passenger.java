package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

public class Passenger {

  private final String passengerEmail;
  private final String passengerName;
  private final Set<Ticket> tickets = new HashSet<>();

  public Passenger(String passengerEmail, String passengerName) {
    this.passengerEmail = requireNonNull(passengerEmail, "Passenger email cannot be null");
    this.passengerName = requireNonNull(passengerName, "Passenger name cannot be null");
    addToPassengersMap(this);
  }

  private void addToPassengersMap(Passenger passenger) {
    passengers.put(passenger.passengerEmail, passenger);
  }

  public String getPassengerEmail() {
    return passengerEmail;
  }

  public String getPassengerName() {
    return passengerName;
  }

  public Set<Ticket> getTickets() {
    return tickets;
  }
}
