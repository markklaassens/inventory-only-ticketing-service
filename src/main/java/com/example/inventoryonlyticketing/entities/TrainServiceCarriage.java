package com.example.inventoryonlyticketing.entities;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainServiceCarriage {

  private final String serviceCarriageId;
  private final String serviceCarriageLabel;
  private final CarriageType serviceCarriageType;
  private final TrainService trainService;
  private final Set<Integer> seatNumbers;

  public TrainServiceCarriage(
      final String trainServiceCarriageLabel,
      final CarriageType trainServiceCarriageType,
      final int amountOfSeats,
      final TrainService trainService
  ) {
    this.serviceCarriageId = requireNonNull(trainServiceCarriageLabel, "Service carriage label cannot be null")
        + "-" + requireNonNull(trainService.getServiceId(), "Train service ID cannot be null");
    this.serviceCarriageLabel = trainServiceCarriageLabel;
    this.serviceCarriageType = requireNonNull(trainServiceCarriageType, "Service carriage type cannot be null");
    this.seatNumbers = IntStream.rangeClosed(1, amountOfSeats).boxed().collect(Collectors.toSet());
    this.trainService =  requireNonNull(trainService, "Train service cannot be null");
  }

  public String getTrainServiceCarriageId() {
    return serviceCarriageId;
  }

  public String getTrainServiceCarriageLabel() {
    return serviceCarriageLabel;
  }

  public CarriageType getTrainServiceCarriageType() {
    return serviceCarriageType;
  }

  public Set<Integer> getSeatNumbers() {
    return seatNumbers;
  }

  public TrainService getTrainService() {
    return trainService;
  }
}
