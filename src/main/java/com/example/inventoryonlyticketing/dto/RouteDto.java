package com.example.inventoryonlyticketing.dto;

import static java.util.Objects.requireNonNull;

import com.example.inventoryonlyticketing.entities.Route;
import com.example.inventoryonlyticketing.entities.Station;
import java.util.List;

public record RouteDto(String routeId, List<String> stationList, int amountOfServicesNeeded) {

  public RouteDto {
    requireNonNull(routeId, "Route ID cannot be null");
    stationList = List.copyOf(requireNonNull(stationList));
  }

  public static RouteDto from(Route route) {
    requireNonNull(route, "Route cannot be null");
    return new RouteDto(
        route.getRouteId(),
        route.getStationList().stream()
            .map(Station::getStationName)
            .toList(),
        route.getAmountOfServicesNeeded());
  }
}
