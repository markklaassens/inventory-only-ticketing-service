package com.example.inventoryonlyticketing.utilities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.carriages;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stations;
import static com.example.inventoryonlyticketing.utilities.UserInterface.makeReservation;

import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.entities.Carriage;
import com.example.inventoryonlyticketing.entities.Passenger;
import com.example.inventoryonlyticketing.entities.Route;
import com.example.inventoryonlyticketing.entities.Service;
import com.example.inventoryonlyticketing.entities.Station;
import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.IntStream;

public class DataInitializer {

  public static void initializeData() {
    initializeStations();
    initializeRoutes();
    initializeCarriages();
    initializeServices();
  }

  private static void initializeStations() {
    new Station("paris001", "Paris Gare du Nord");
    new Station("london001", "London Waterloo");
    new Station("amsterdam001", "Amsterdam Centraal");
    new Station("berlin001", "Berlin Hauptbahnhof");
    new Station("calais001", "Calais Ville");
  }

  private static void initializeRoutes() {
    new Route("LP", List.of(stations.get("london001"), stations.get("paris001")));
    new Route("PC", List.of(stations.get("paris001"), stations.get("calais001")));
    new Route("PA", List.of(stations.get("paris001"), stations.get("amsterdam001")));
    new Route("AB", List.of(stations.get("amsterdam001"), stations.get("berlin001")));
    new Route("LPA", List.of(stations.get("london001"), stations.get("paris001"), stations.get("amsterdam001")));
  }

  private static void initializeCarriages() {
    new Carriage("A1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("B4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("C1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("D4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("E1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("F4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("G1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("H4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("I1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("J1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("K4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("L1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("M4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("N1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("O4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("P4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("Q1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("R4321", CarriageType.SECOND_CLASS, 20);
    new Carriage("S1234", CarriageType.FIRST_CLASS, 20);
    new Carriage("T4321", CarriageType.SECOND_CLASS, 20);
  }

  private static void initializeServices() {
    try {
      new Service(
          "AP1-6720",
          6720,
          stations.get("london001"),
          stations.get("paris001"),
          new HashSet<>(carriages.values()),
          ZonedDateTime.of(
              LocalDate.of(2021, 4, 1),
              LocalTime.of(12, 0, 0),
              ZoneId.of("Europe/London")
          ),
          ZonedDateTime.of(
              LocalDate.of(2021, 4, 1),
              LocalTime.of(18, 30, 0),
              ZoneId.of("Europe/Paris")
          )
      );
      new Service(
          "AP2-5160",
          5160,
          stations.get("paris001"),
          stations.get("amsterdam001"),
          new HashSet<>(carriages.values()),
          ZonedDateTime.of(
              LocalDate.of(2021, 4, 2),
              LocalTime.of(19, 0, 0),
              ZoneId.of("Europe/Paris")),
          ZonedDateTime.of(
              LocalDate.of(
                  2021, 4, 1),
              LocalTime.of(23, 30, 0),
              ZoneId.of("Europe/Amsterdam")
          )
      );
      new Service(
          "AP2-4380",
          4380,
          stations.get("amsterdam001"),
          stations.get("berlin001"),
          new HashSet<>(carriages.values()),
          ZonedDateTime.of(
              LocalDate.of(2021, 4, 2),
              LocalTime.of(0, 30, 0),
              ZoneId.of("Europe/Amsterdam")
          ),
          ZonedDateTime.of(
              LocalDate.of(2021, 4, 1),
              LocalTime.of(18, 30, 0),
              ZoneId.of("Europe/Berlin")
          )
      );
      new Service(
          "D20-5160",
          5160,
          stations.get("paris001"),
          stations.get("calais001"),
          new HashSet<>(carriages.values()),
          ZonedDateTime.of(
              LocalDate.of(2021, 12, 20),
              LocalTime.of(19, 0, 0),
              ZoneId.of("Europe/Paris")),
          ZonedDateTime.of(
              LocalDate.of(
                  2021, 12, 20),
              LocalTime.of(23, 0, 0),
              ZoneId.of("Europe/Paris")
          )
      );
    } catch (NoSuchElementException | IllegalArgumentException e) {
      System.out.printf("Failed to create service: %s.%n".formatted(e.getMessage()));
    }
  }

  public static void initializeBookings() {
    final var integerList = IntStream.rangeClosed(1, 12).boxed().toList();
    initializePassengers(integerList);
    initializeTwelveCalaisToParisBookings(integerList);
  }

  private static void initializePassengers(List<Integer> integerList) {
    new Passenger("harry.pompenaar@example.com", "Harry Pompenaar");
    new Passenger("jeroen.nitdoen@example.com", "Jeroen Nitdoen");
    for (final var i : integerList) {
      new Passenger("test" + i + "@example.com", "Test " + i);
    }
  }

  private static void initializeTwelveCalaisToParisBookings(List<Integer> integerList) {
    final var serviceId = "D20-5160";
    final Queue<SeatDto> seatDtoQueue = new LinkedList<>();
    for (final var i : integerList) {
      seatDtoQueue.add(new SeatDto("A", i));
    }
    try {
      passengers.keySet().stream()
          .filter(pe -> pe.startsWith("test"))
          .sorted(Comparator.comparingInt(pe -> {
            final var numberPart = pe.substring(4, pe.indexOf('@'));
            return Integer.parseInt(numberPart);  // Convert the extracted number to an integer
          })).forEach(pe -> {
            final var seatDto = Optional.ofNullable(seatDtoQueue.poll()).orElseThrow(
                () -> new InputMismatchException(
                    "No booking can't be made for passenger with email %s, no more seatDtos.%n".formatted(pe))
            );
            makeReservation(pe, Map.of(serviceId, List.of(seatDto)));
          });
    } catch (InputMismatchException e) {
      System.out.println("An error occurred while initializing 10 bookings from Paris to Calais " + e.getMessage());
    }
  }
}
