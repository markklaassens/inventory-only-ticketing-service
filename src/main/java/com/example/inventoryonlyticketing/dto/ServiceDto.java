package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.Service;
import java.time.ZonedDateTime;

public record ServiceDto(
    String serviceId,
    String departureStation,
    ZonedDateTime departureTime,
    String arrivalStation,
    ZonedDateTime arrivalTime,
    int passengerCapacity
) {

  public ServiceDto {
    requireNonNull(serviceId, "Service ID cannot be null");
    requireNonNull(departureStation, "Departure station cannot be null");
    requireNonNull(departureTime, "Departure time cannot be null");
    requireNonNull(arrivalStation, "Arrival station cannot be null");
    requireNonNull(arrivalTime, "Arrival time cannot be null");
  }

  public static ServiceDto from(Service service) {
    requireNonNull(service, "Service cannot be null");
    return new ServiceDto(
        service.getServiceId(),
        service.getDepatureStation().getStationName(),
        service.getDepartureTime(),
        service.getArrivalStation().getStationName(),
        service.getArrivalTime(),
        service.getPassengerTotal()
    );
  }

}
