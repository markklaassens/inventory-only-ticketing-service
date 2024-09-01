package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.carriages;
import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.util.HashSet;
import java.util.Set;

public class Carriage {

  private final String carriageId;
  private final CarriageType carriageType;
  private final int amountOfSeats;
  private final Set<ServiceCarriage> serviceCarriages = new HashSet<>();

  public Carriage(String carriageId, CarriageType carriageType, int amountOfSeats) {
    this.carriageId = requireNonNull(carriageId, "Carriage ID cannot be null");
    this.carriageType = requireNonNull(carriageType, "Carriage type cannot be null");
    this.amountOfSeats = amountOfSeats;
    addToCarriagesMap(this);
  }

  private void addToCarriagesMap(Carriage carriage) {
    carriages.put(carriage.getCarriageId(), carriage);
  }

  public String getCarriageId() {
    return carriageId;
  }

  public CarriageType getCarriageType() {
    return carriageType;
  }

  public int getAmountOfSeats() {
    return amountOfSeats;
  }

  public Set<ServiceCarriage> getServiceCarriages() {
    return serviceCarriages;
  }
}
