package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.TrainService;
import java.time.LocalDateTime;

public record TrainServiceDto(
    String serviceId,
    String departureStation,
    LocalDateTime departureTime,
    String arrivalStation,
    LocalDateTime arrivalTime,
    int passengerCapacity
) {

  public TrainServiceDto {
    requireNonNull(serviceId, "Service ID cannot be null");
    requireNonNull(departureStation, "Departure station cannot be null");
    requireNonNull(departureTime, "Departure time cannot be null");
    requireNonNull(arrivalStation, "Arrival station cannot be null");
    requireNonNull(arrivalTime, "Arrival time cannot be null");
  }

  public static TrainServiceDto from(TrainService trainService) {
    requireNonNull(trainService, "Service cannot be null");
    return new TrainServiceDto(
        trainService.getServiceId(),
        trainService.getDepatureStation().getStationName(),
        trainService.getDepartureTime(),
        trainService.getArrivalStation().getStationName(),
        trainService.getArrivalTime(),
        trainService.getPassengerTotal()
    );
  }

}
