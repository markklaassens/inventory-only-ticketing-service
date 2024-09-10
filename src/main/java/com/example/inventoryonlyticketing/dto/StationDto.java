package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.Station;

public record StationDto(String stationId, String stationName) {

  public StationDto {
    requireNonNull(stationId, "Station ID cannot be null");
    requireNonNull(stationName, "Station name cannot be null");
  }

  public static StationDto from(Station station) {
    requireNonNull(station, "Route cannot be null");
    return new StationDto(
        station.getStationId(),
        station.getStationName()
    );
  }
}
