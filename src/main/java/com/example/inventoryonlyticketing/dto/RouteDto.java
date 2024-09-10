package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import java.util.List;

public record RouteDto(String routeId, int amountOfStations, List<StationDto> stationDtoList) {

  public RouteDto {
    requireNonNull(routeId);
    stationDtoList = List.copyOf(requireNonNull(stationDtoList));
  }

  public static RouteDto from(int routeIndex, List<StationDto> stationDtoList) {
    return new RouteDto(
        stationDtoList.getFirst().stationName().substring(0, 2).toUpperCase() + "-" +
            stationDtoList.getLast().stationName().substring(0, 2).toUpperCase() + routeIndex,
        stationDtoList.size(),
        requireNonNull(stationDtoList)
    );
  }
}
