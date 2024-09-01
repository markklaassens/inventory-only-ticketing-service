package com.example.inventoryonlyticketing.entities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.routes;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.services;
import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Service {

  private final String serviceId;
  private final int serviceNumber;
  private final Station depatureStation;
  private final Station arrivalStation;
  private final Set<ServiceCarriage> serviceCarriages;
  private final ZonedDateTime departureTime;
  private final ZonedDateTime arrivalTime;
  private final Set<Route> associatedRoutes = new HashSet<>();
  private int passengerTotal = 0;

  public Service(
      String serviceId,
      int serviceNumber,
      Station depatureStation,
      Station arrivalStation,
      Set<Carriage> carriages,
      ZonedDateTime departureTime,
      ZonedDateTime arrivalTime) {
    this.serviceId = requireNonNull(serviceId, "Service ID cannot be null");
    this.serviceNumber = serviceNumber;
    this.depatureStation = requireNonNull(depatureStation, "Departure Station cannot be null");
    this.arrivalStation = requireNonNull(arrivalStation, "Arrival Station cannot be null");
    this.departureTime = requireNonNull(departureTime, "Departure Time cannot be null");
    this.arrivalTime = requireNonNull(arrivalTime, "Arrival Time cannot be null");
    this.serviceCarriages = createServiceCarriages(requireNonNull(carriages, "Carriages cannot be null"));
    addToRoutesAsSegment(this);
    addToServicesMap(this);
  }

  private void addToServicesMap(Service service) {
    services.put(service.serviceId, service);
  }

  private void addToRoutesAsSegment(Service service) {
    routes.values().stream()
        .filter(route -> route.getStationList().size() > 1)
        .filter(route -> route.getStationList().subList(0, route.getStationList().size() - 1)
            .contains(service.getDepatureStation()))
        .filter(
            route -> route.getStationList().get(route.getStationList().indexOf(depatureStation) + 1).equals(arrivalStation))
        .peek(route -> {
          final var segmentKey = route.getStationList().indexOf(depatureStation) + 1;
          route.getSegmentServicesMap().computeIfAbsent(segmentKey, k -> new LinkedHashSet<>()).add(service);
          associatedRoutes.add(route);
        }).findAny().orElseThrow(() -> new NoSuchElementException("No routes found for the given service!"));
  }

  private Set<ServiceCarriage> createServiceCarriages(Set<Carriage> carriages) {
    final var serviceCarriages = new LinkedHashSet<ServiceCarriage>();
    final var currentKey = new AtomicReference<>('A');
    carriages.stream()
        .sorted(Comparator.comparing(Carriage::getCarriageType))
        .forEach(carriage -> {
          ServiceCarriage serviceCarriage = new ServiceCarriage(
              String.valueOf(currentKey.get()),
              carriage.getCarriageType(),
              this,
              carriage,
              carriage.getAmountOfSeats());
          serviceCarriages.add(serviceCarriage);
          carriage.getServiceCarriages().add(serviceCarriage);
          currentKey.set((char) (currentKey.get() + 1));
        });
    return serviceCarriages;
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

  public Set<ServiceCarriage> getServiceCarriages() {
    return serviceCarriages;
  }

  public ZonedDateTime getDepartureTime() {
    return departureTime;
  }

  public ZonedDateTime getArrivalTime() {
    return arrivalTime;
  }

  public int getPassengerTotal() {
    return passengerTotal;
  }

  public void increasePassengerTotal() {
    this.passengerTotal += 1;
  }

  public Set<Route> getAssociatedRoutes() {
    return associatedRoutes;
  }
}
