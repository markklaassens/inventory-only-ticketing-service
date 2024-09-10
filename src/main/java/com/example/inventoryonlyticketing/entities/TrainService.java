package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.trainServices;
import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TrainService {

  private final String serviceId;
  private final int serviceNumber;
  private final Station depatureStation;
  private final Station arrivalStation;
  private final Set<TrainServiceCarriage> trainServiceCarriages;
  private final LocalDateTime departureTime;
  private final LocalDateTime arrivalTime;
  private int passengerTotal = 0;

  public TrainService(
      int serviceNumber,
      String serviceId,
      Station depatureStation,
      Station arrivalStation,
      int amountOfCarriages,
      LocalDateTime departureTime,
      LocalDateTime arrivalTime) {
    this.serviceNumber = serviceNumber;
    this.serviceId = requireNonNull(serviceId, "Service ID cannot be null");
    this.depatureStation = requireNonNull(depatureStation, "Departure Station cannot be null");
    this.arrivalStation = requireNonNull(arrivalStation, "Arrival Station cannot be null");
    this.departureTime = requireNonNull(departureTime, "Departure Time cannot be null");
    this.arrivalTime = requireNonNull(arrivalTime, "Arrival Time cannot be null");
    this.trainServiceCarriages = createServiceCarriages(amountOfCarriages);
    addToServicesMap(this);
  }

  private Set<TrainServiceCarriage> createServiceCarriages(int amountOfCarriages) {
    final var serviceCarriages = new LinkedHashSet<TrainServiceCarriage>();
    final var currentKey = new AtomicReference<>('A');
    for (int i = 0; i < amountOfCarriages; i++) {
      CarriageType carriageType;
      if (i <= (amountOfCarriages / 2)) {
        carriageType = CarriageType.FIRST_CLASS;
      } else {
        carriageType = CarriageType.SECOND_CLASS;
      }
      final var trainServiceCarriage = new TrainServiceCarriage(
          String.valueOf(currentKey.get()),
          carriageType,
          carriageType.getAmountOfSeats(),
          this
      );
      serviceCarriages.add(trainServiceCarriage);
    }
    return serviceCarriages;
  }

  private void addToServicesMap(final TrainService service) {
    trainServices.put(service.serviceId, service);
  }

  public String getServiceId() {
    return serviceId;
  }

  public int getServiceNumber() {
    return serviceNumber;
  }

  public Station getDepatureStation() {
    return depatureStation;
  }

  public Station getArrivalStation() {
    return arrivalStation;
  }

  public Set<TrainServiceCarriage> getServiceCarriages() {
    return trainServiceCarriages;
  }

  public LocalDateTime getDepartureTime() {
    return departureTime;
  }

  public LocalDateTime getArrivalTime() {
    return arrivalTime;
  }

  public int getPassengerTotal() {
    return passengerTotal;
  }

  public void increasePassengerTotal() {
    this.passengerTotal += 1;
  }

}
