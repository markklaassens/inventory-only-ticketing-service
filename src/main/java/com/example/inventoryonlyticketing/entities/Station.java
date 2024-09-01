package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stations;
import static java.util.Objects.requireNonNull;

public class Station {

  private final String stationId;
  private final String stationName;
  private int departingPassengers;
  private int arrivingPassengers;

  public Station(String stationId, String stationName) {
    this.stationId = requireNonNull(stationId, "Station ID cannot be null");
    this.stationName = requireNonNull(stationName, "Station name cannot be null");
    addToStationsMap(this);
  }

  private void addToStationsMap(Station station) {
    stations.put(stationId, station);
  }

  public String getStationId() {
    return stationId;
  }

  public String getStationName() {
    return stationName;
  }

  public int getDepartingPassengers() {
    return departingPassengers;
  }

  public void increaseDepartingPassengers() {
    this.departingPassengers += 1;
  }

  public int getArrivingPassengers() {
    return arrivingPassengers;
  }

  public void increaseArrivingPassengers() {
    this.arrivingPassengers += 1;
  }
}
