package com.example.inventoryonlyticketing.services;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.services;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stations;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.tickets;

import com.example.inventoryonlyticketing.dto.PassengerDto;
import com.example.inventoryonlyticketing.dto.ServiceDto;
import com.example.inventoryonlyticketing.entities.Service;
import com.example.inventoryonlyticketing.entities.Station;
import com.example.inventoryonlyticketing.exceptions.InsufficientSearchParametersException;
import com.example.inventoryonlyticketing.exceptions.StationNotFoundException;
import com.example.inventoryonlyticketing.exceptions.TicketNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InfoService {

  public Map<String, Map<String, Integer>> getPassengersAtStations() {
    final var passengersAtStations = new LinkedHashMap<String, Map<String, Integer>>();
    for (final var stationName : stations.keySet()) {
      final var station = stations.get(stationName);
      final var passengersAtStation = new LinkedHashMap<String, Integer>();
      passengersAtStation.put("Arriving", station.getArrivingPassengers());
      passengersAtStation.put("Departing", station.getDepartingPassengers());
      passengersAtStations.put(station.getStationName(), passengersAtStation);
    }
    return passengersAtStations;
  }

  public ServiceDto getServiceInfo(String serviceId) {
    return ServiceDto.from(services.get(serviceId));
  }

  public Map<List<String>, Integer> getAmountOfPassengersBetweenTwoStations(String departureStationId,
      String arrivalStationId) {
    final var passengersBetweenStations = new HashMap<List<String>, Integer>();

    final var departureStation = getStation(departureStationId);
    final var arrivalStation = getStation(arrivalStationId);

    final int totalPassengers = services.values().stream()
        .filter(s -> s.getDepatureStation().equals(departureStation) && s.getArrivalStation().equals(arrivalStation))
        .map(Service::getPassengerTotal)
        .reduce(0, Integer::sum);

    passengersBetweenStations.put(List.of(
        departureStation.getStationName(), arrivalStation.getStationName()), totalPassengers);
    return passengersBetweenStations;
  }

  private Station getStation(String stationId) {
    return Optional.ofNullable(stations.get(stationId)).orElseThrow(
        () -> new StationNotFoundException("Station with id " + stationId + " not found.%n")
    );
  }

  public PassengerDto getPassengerByArrivalStationDateServiceNumberAndSeat(
      String arrivalStationId,
      LocalDate arrivalDate,
      Integer serviceNumber,
      String seat
  ) {
    final var arrivalStation = getStation(arrivalStationId);
    final var ticketList = tickets.values().stream()
        .filter(ticket -> ticket.getArrivalTime().getDayOfMonth() == arrivalDate.getDayOfMonth())
        .filter(ticket -> ticket.getArrivalTime().getMonthValue() == arrivalDate.getMonthValue())
        .filter(ticket -> ticket.getArrivalTime().getYear() == arrivalDate.getYear())
        .filter(ticket -> ticket.getArrivalStation().equals(arrivalStation))
        .filter(ticket -> ticket.getSeat().getServiceCarriageId().equals(seat.substring(0, 1)))
        .filter(ticket -> ticket.getSeat().getSeatNumber() == (Integer.parseInt(seat.substring(1))))
        .filter(ticket -> ticket.getService().getServiceNumber() == serviceNumber)
        .toList();
    if (ticketList.size() > 1) {
      throw new InsufficientSearchParametersException(
          "Search on arrival station %s, arrival date %s, and seat %s returned multiple tickets.%n");
    } else if (ticketList.isEmpty()) {
      throw new TicketNotFoundException("Ticket is not yet sold.%n");
    }
    return new PassengerDto(ticketList.getFirst().getPassenger().getPassengerEmail(),
        ticketList.getFirst().getPassenger().getPassengerName());
  }
}
