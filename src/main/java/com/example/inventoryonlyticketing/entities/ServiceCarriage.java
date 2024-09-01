package com.example.inventoryonlyticketing.entities;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServiceCarriage {

  private final String serviceCarriageId;
  private final String serviceCarriageLabel;
  private final CarriageType serviceCarriageType;
  private final Service service;
  private final Carriage carriage;
  private final Set<Integer> seatNumbers;

  public ServiceCarriage(
      String serviceCarriageLabel,
      CarriageType serviceCarriageType,
      Service service,
      Carriage carriage,
      int amountOfSeats) {
    requireNonNull(service, "Service cannot be null");
    requireNonNull(carriage, "Carriage cannot be null");
    this.serviceCarriageId = service.getServiceId() + carriage.getCarriageId();
    this.serviceCarriageLabel = requireNonNull(serviceCarriageLabel, "Service carriage label cannot be null");
    this.serviceCarriageType = requireNonNull(serviceCarriageType, "Service carriage type cannot be null");
    this.service = service;
    this.carriage = carriage;
    this.seatNumbers = IntStream.rangeClosed(1, amountOfSeats).boxed().collect(Collectors.toSet());
  }

  public String getServiceCarriageId() {
    return serviceCarriageId;
  }

  public String getServiceCarriageLabel() {
    return serviceCarriageLabel;
  }

  public CarriageType getServiceCarriageType() {
    return serviceCarriageType;
  }

  public Service getService() {
    return service;
  }

  public Carriage getCarriage() {
    return carriage;
  }

  public Set<Integer> getSeatNumbers() {
    return seatNumbers;
  }
}
