package com.example.inventoryonlyticketing.entities;

import static java.util.Objects.requireNonNull;

public class Seat {

  private final String serviceCarriageId;
  private final int seatNumber;
  private Passenger passenger;

  public Seat(String serviceCarriageId, int seatNumber) {
    this.serviceCarriageId = requireNonNull(serviceCarriageId, "Service carriage ID cannot be null");
    this.seatNumber = seatNumber;
  }

  public String getServiceCarriageId() {
    return serviceCarriageId;
  }

  public int getSeatNumber() {
    return seatNumber;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public void setPassenger(Passenger passenger) {
    if (this.passenger != null) {
      throw new IllegalStateException("Passenger is already set, for seat " + serviceCarriageId + seatNumber);
    } else
      this.passenger = passenger;
  }
}
