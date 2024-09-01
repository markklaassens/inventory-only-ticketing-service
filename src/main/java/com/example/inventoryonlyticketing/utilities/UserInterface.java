package com.example.inventoryonlyticketing.utilities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;

import com.example.inventoryonlyticketing.dto.BookingRequestDto;
import com.example.inventoryonlyticketing.dto.ResponseDto;
import com.example.inventoryonlyticketing.dto.RouteDto;
import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.dto.ServiceDto;
import com.example.inventoryonlyticketing.entities.Passenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class UserInterface {

  private static final HTTPServiceClient HTTP_CLIENT = new HTTPServiceClient();
  private static final Scanner SCANNER = new Scanner(System.in);

  public static void bookParisAmsterdam() {
    System.out.println(HTTP_CLIENT.get("/routes")); // First we get all the routes
    System.out.println(HTTP_CLIENT.get("/services/PA/1")); // Second all services on the route
    System.out.println(HTTP_CLIENT.get("/seats/AP2-5160")); // Then we get all seats available for the first service

    // Selecting seats
    final var seatDto1 = new SeatDto("A", 11);
    final var seatDto2 = new SeatDto("A", 12);

    makeReservation("harry.pompenaar@example.com", Map.of("AP2-5160", List.of(seatDto1, seatDto2)));
  }

  public static void makeReservation(String passengerEmail, Map<String, List<SeatDto>> seatMap) {
    final var postBookingResultResponse =
        (ResponseDto) HTTP_CLIENT.post("/reservation", new BookingRequestDto(passengerEmail, seatMap));
    if (postBookingResultResponse.getStatusCode() != 201) {
      System.out.println("An error occured. Status code: " + postBookingResultResponse.getStatusCode()
          + " Message: " + postBookingResultResponse.getBody());
    } else {
      System.out.printf("Status code: %s, Booking successful! %n%s %n%n",
          postBookingResultResponse.getStatusCode(), postBookingResultResponse.getBody());
    }
  }

  public static void bookLondonAmsterdam() {
    System.out.println(HTTP_CLIENT.get("/routes")); // First we get all the routes
    System.out.println(HTTP_CLIENT.get("/services/LPA/1")); // Second all services on the first segment of the route
    System.out.println(HTTP_CLIENT.get("/seats/AP1-6720")); // Then we get all seats available for the first service

    // Selecting seats
    final var seatDto1 = new SeatDto("H", 1);
    final var seatDto2 = new SeatDto("N", 5);

    System.out.println(HTTP_CLIENT.get("/services/LPA/2")); // All services on the second segment of the route
    System.out.println(HTTP_CLIENT.get("/seats/AP2-5160")); // Then we get all seats available for the second service

    // Selecting seats
    final var seatDto3 = new SeatDto("A", 1);
    final var seatDto4 = new SeatDto("T", 7);

    makeReservation("jeroen.nitdoen@example.com",
        Map.of("AP1-6720", List.of(seatDto1, seatDto2), "AP2-5160", List.of(seatDto3, seatDto4)));
  }

  public static void getInfo() {
    System.out.println("Getting arriving and departing passengers for all stations");
    System.out.printf(HTTP_CLIENT.get("/stations") + "%n%n");
    System.out.println("Getting passengers travelling from 'Paris' to 'Calais'");
    System.out.printf(HTTP_CLIENT.get("/stations/paris001/calais001") + "%n%n");
    System.out.println(
        "Getting passenger travelling to 'Calais' with service '5160' on '20-12-2021' sitting on seat 'A11'.");
    System.out.printf(HTTP_CLIENT.get("/stations/calais001/2021-12-20/5160/A11") + "%n%n");
  }

  public static void book() {
    final var yourPassenger = createPassenger(); // Create your passenger
    final var serviceSeatMap = new HashMap<String, List<SeatDto>>();
    final var allRoutes = getRoutes();
    final var routeDto = selectRoute(allRoutes);

    for (int i = 0; i < routeDto.amountOfServicesNeeded(); i++) {
      final var segment = i + 1;
      final var serviceDtoList = getServices(segment, routeDto);
      final var serviceDto = selectService(serviceDtoList);
      final var seatDtoList = selectSeats(serviceDto);
      serviceSeatMap.put(serviceDto.serviceId(), seatDtoList);
    }
    makeReservation(yourPassenger.getPassengerEmail(), serviceSeatMap);
  }

  private static Passenger createPassenger() {
    System.out.println("Create your passenger, please write your email address followed by an enter.");
    final var email = setEmail();
    System.out.println("Great, now your name please!");
    final var name = SCANNER.nextLine();
    final var newPassenger = new Passenger(email, name);
    passengers.put(newPassenger.getPassengerEmail(), newPassenger);
    return newPassenger;
  }

  private static String setEmail() {
    String email;
    while (true) {
      email = SCANNER.nextLine();
      if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        System.out.println("Invalid email address. Please enter a valid email address, example 'test@example.com'.");
      } else if (passengers.containsKey(email)) {
        System.out.println("Email already in use. Please choose another one.");
      } else {
        return email;
      }
    }
  }

  private static List<RouteDto> getRoutes() {
    System.out.println("Getting all the available routes.");
    final var getRoutesResponse = (ResponseDto) HTTP_CLIENT.get("/routes");
    if (getRoutesResponse.getStatusCode() != 200) {
      System.out.println("Something went wrong. Please try again Status code: " + getRoutesResponse.getStatusCode()
          + " Message: " + getRoutesResponse.getBody());
      book();
      return List.of();
    } else {
      @SuppressWarnings("unchecked") final List<RouteDto> allRoutes = (List<RouteDto>) getRoutesResponse.getBody();
      if (allRoutes.isEmpty()) {
        System.out.println("Something went wrong. No Routes found. Terminating reservation system!");
        System.exit(-1);
        return List.of();
      } else {
        System.out.println(allRoutes);
        return allRoutes;
      }
    }
  }

  private static RouteDto selectRoute(List<RouteDto> routes) {
    while (true) {
      System.out.println("Please enter the route ID of the route you would like to book, example 'PA'.");
      final var routeId = SCANNER.nextLine();
      final var optionalRouteDto = routes.stream().filter(route -> route.routeId().equals(routeId)).findFirst();
      if (optionalRouteDto.isEmpty()) {
        System.out.println("Try again, please enter a valid route ID.");
      } else {
        return optionalRouteDto.get();
      }
    }
  }

  private static List<ServiceDto> getServices(int segment, RouteDto routeDto) {
    System.out.printf("Booking service %s out of %s services needed.%n", segment, routeDto.amountOfServicesNeeded());
    System.out.printf("Getting all the available services on segment %s on route %s.%n", segment, routeDto.routeId());
    final var getServicesResponse = (ResponseDto) HTTP_CLIENT.get("/services/" + routeDto.routeId() + "/" + segment);
    if (getServicesResponse.getStatusCode() != 200) {
      System.out.println("Something went wrong. Please try again. Status code: " + getServicesResponse.getStatusCode()
          + " Message: " + getServicesResponse.getBody());
      book();
      return List.of();
    } else {
      @SuppressWarnings("unchecked") final List<ServiceDto> serviceDtoList = (List<ServiceDto>) getServicesResponse.getBody();
      if (serviceDtoList.isEmpty()) {
        System.out.println("Something went wrong. No Services found. Terminating reservation system!");
        System.exit(-1);
        return List.of();
      } else {
        System.out.println(serviceDtoList);
        return serviceDtoList;
      }
    }
  }

  private static ServiceDto selectService(List<ServiceDto> services) {
    while (true) {
      System.out.println("Please enter the service id you would like to book, example 'JA1-4321'.");
      final var serviceId = SCANNER.nextLine();
      final var optionalServiceDto = services.stream().filter(service -> service.serviceId().equals(serviceId)).findFirst();
      if (optionalServiceDto.isEmpty()) {
        System.out.println("Try again, please enter a valid service ID.");
      } else {
        return optionalServiceDto.get();
      }
    }
  }

  private static List<SeatDto> selectSeats(ServiceDto serviceDto) {
    getSeats(serviceDto.serviceId());
    final var amountOfSeats = setAmountOfSeats();
    final var seatDtoList = new ArrayList<SeatDto>();
    createSeatList(seatDtoList, amountOfSeats);
    return seatDtoList;
  }

  private static void getSeats(String serviceId) {
    System.out.println("Getting available seats for service " + serviceId);
    final var getSeatsResponse = (ResponseDto) HTTP_CLIENT.get("/seats/" + serviceId);
    if (getSeatsResponse.getStatusCode() != 200) {
      System.out.println("Something went wrong. Please try again. Status code: " + getSeatsResponse.getStatusCode()
          + " Message: " + getSeatsResponse.getBody());
      book();
    } else {
      @SuppressWarnings("unchecked") final var carriageSeatMap = (Map<String, Map<String, Set<Integer>>>) getSeatsResponse.getBody();
      if (carriageSeatMap.isEmpty()) {
        System.out.println("Something went wrong. No Services found. Terminating reservation system!");
        System.exit(-1);
      } else {
        System.out.println(carriageSeatMap);
      }
    }
  }

  private static int setAmountOfSeats() {
    int amountOfSeats;
    while (true) {
      try {
        System.out.println("Please enter the amount of seats you want to book, example '2'.");
        amountOfSeats = Integer.parseInt(SCANNER.nextLine());
        break;
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a valid integer.");
      }
    }
    return amountOfSeats;
  }

  private static void createSeatList(List<SeatDto> seats, int amountOfSeats) {
    for (int j = 0; j < amountOfSeats; j++) {
      System.out.printf("Booking seat %s out of %s.%n", j + 1, amountOfSeats);
      while (true) {
        System.out.println("Please enter the seat number you would like to book, example 'B10'.");
        final var seatString = SCANNER.nextLine();
        if (!seatString.matches("^[A-Z]\\d+$")) {
          System.out.println("Invalid seat format. Expected format: <CarriageID><SeatNumber> (e.g., 'A12').");
          continue;
        }
        seats.add(SeatDto.fromString(seatString));
        break;
      }
    }
  }

  public static boolean newBooking() {
    System.out.println("Create another booking? (Y/N): ");
    while (true) {
      final var answer = SCANNER.nextLine();
      if (answer.equalsIgnoreCase("Y")) {
        return true;
      } else if (answer.equalsIgnoreCase("N")) {
        return false;
      } else {
        System.out.println("Invalid answer, enter 'Y' to continue, or 'N' to exit.");
      }
    }
  }
}
