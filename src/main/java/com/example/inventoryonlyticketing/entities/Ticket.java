package com.example.inventoryonlyticketing.entities;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Ticket {

  private final UUID ticketId;
  private final TrainService trainService;
  private final Passenger passenger;
  private final Seat seat;
  private final CarriageType carriageType;
  private final Station departureStation;
  private final ZonedDateTime departureTime;
  private final Station arrivalStation;
  private final ZonedDateTime arrivalTime;
  private Booking booking;

  public Ticket(
      TrainService trainService,
      Passenger passenger,
      CarriageType carriageType,
      Seat seat,
      Station departureStation,
      ZonedDateTime departureTime,
      Station arrivalStation,
      ZonedDateTime arrivalTime
  ) {
    this.ticketId = randomUUID();
    this.trainService = requireNonNull(trainService, "Service must not be null");
    this.passenger = requireNonNull(passenger, "Passenger cannot be null");
    this.carriageType = requireNonNull(carriageType, "Carriage type cannot be null");
    this.seat = requireNonNull(seat, "Seat cannot be null");
    this.departureStation = requireNonNull(departureStation, "Departure Station cannot be null");
    this.departureTime = requireNonNull(departureTime,  "Departure Time cannot be null");
    this.arrivalStation = requireNonNull(arrivalStation, "Arrival Station cannot be null");
    this.arrivalTime = requireNonNull(arrivalTime,  "Arrival Time cannot be null");
  }

  public UUID getTicketId() {
    return ticketId;
  }

  public TrainService getTrainService() {
    return trainService;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public Seat getSeat() {
    return seat;
  }

  public CarriageType getCarriageType() {
    return carriageType;
  }

  public Station getDepartureStation() {
    return departureStation;
  }

  public ZonedDateTime getDepartureTime() {
    return departureTime;
  }

  public Station getArrivalStation() {
    return arrivalStation;
  }

  public ZonedDateTime getArrivalTime() {
    return arrivalTime;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    if (this.booking != null) {
      throw new IllegalStateException("Booking already set for ticket " + ticketId);
    }
    this.booking = booking;
  }
}
