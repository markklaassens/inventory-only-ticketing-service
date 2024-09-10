package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.Ticket;
import java.time.ZonedDateTime;

public record TicketDto(
    String ticketId,
    String serviceId,
    String passengerName,
    String carriageType,
    String seat,
    String departureStation,
    ZonedDateTime departureTime,
    String arrivalStation,
    ZonedDateTime arrivalTime
) {

  public TicketDto {
    requireNonNull(ticketId, "Ticket ID cannot be null");
    requireNonNull(serviceId, "Service ID cannot be null");
    requireNonNull(passengerName, "Passenger name cannot be null");
    requireNonNull(carriageType, "Carriage type cannot be null");
    requireNonNull(seat, "Seat type cannot be null");
    requireNonNull(departureStation, "Departure station name cannot be null");
    requireNonNull(departureTime, "Departure time cannot be null");
    requireNonNull(arrivalStation, "Arrival station name cannot be null");
    requireNonNull(arrivalTime, "Arrival time cannot be null");
  }

  public static TicketDto from(Ticket ticket) {
    requireNonNull(ticket, "Ticket cannot be null");
    return new TicketDto(
        ticket.getTicketId().toString(),
        ticket.getTrainService().getServiceId(),
        ticket.getPassenger().getPassengerName(),
        ticket.getCarriageType().getDisplayName(),
        ticket.getSeat().getServiceCarriageId() + ticket.getSeat().getSeatNumber(),
        ticket.getDepartureStation().getStationName(),
        ticket.getDepartureTime(),
        ticket.getArrivalStation().getStationName(),
        ticket.getArrivalTime()
    );
  }
}
