package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.routes;
import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Route {

  private final String routeId;
  private final List<Station> stationList;
  private final LinkedHashMap<Station, Integer> stationDistanceMap;
  private final Map<Integer, Set<Service>> segmentServicesMap;
  private final int amountOfServicesNeeded;

  public Route(String routeId, List<Station> stationList) {
    this.routeId = requireNonNull(routeId, "Route ID cannot be null");
    this.stationList = requireNonNull(stationList, "Station list cannot be null");
    this.stationDistanceMap = generateStationDistanceMap(stationList);
    this.amountOfServicesNeeded =
        stationDistanceMap.size() - 1; // -1 because you need 1 services less than the amount of total stations
    this.segmentServicesMap = new LinkedHashMap<>();
    addToRoutesMap(this);
  }

  private LinkedHashMap<Station, Integer> generateStationDistanceMap(List<Station> stationList) {
    final var stationDistanceMap = new LinkedHashMap<Station, Integer>();
    for (Station station : stationList) {
      stationDistanceMap.put(station, stationList.indexOf(station));
    }
    return stationDistanceMap;
  }

  private void addToRoutesMap(Route route) {
    routes.put(route.routeId, route);
  }

  public String getRouteId() {
    return routeId;
  }

  public List<Station> getStationList() {
    return stationList;
  }

  public LinkedHashMap<Station, Integer> getStationDistanceMap() {
    return stationDistanceMap;
  }

  public Map<Integer, Set<Service>> getSegmentServicesMap() {
    return segmentServicesMap;
  }

  public int getAmountOfServicesNeeded() {
    return amountOfServicesNeeded;
  }
}
