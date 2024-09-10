package com.example.inventoryonlyticketing.utilities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;

import com.example.inventoryonlyticketing.dto.RouteDto;
import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.dto.StationDto;
import com.example.inventoryonlyticketing.entities.Passenger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

  private static final Scanner SCANNER = new Scanner(System.in);
  private static final HttpClient HTTP_CLIENT = new HttpClient();


  public static void getInfo() {
//    System.out.println("Getting arriving and departing passengers for all stations");
//    System.out.printf(HTTP_CLIENT.get("/stations") + "%n%n");
  }

  public static void book() {
    final var yourPassenger = createPassenger(); // Create your passenger
    final var trainServiceSeatMap = new HashMap<String, List<SeatDto>>();
    final var allStations = getStations();
    final var allRoutes = getRoutes(allStations);
    final var departureDateTime = setDepartureDateTime();
    final var route = setRoute(allRoutes);



    for (int i = 0; i < route.amountOfStations(); i++) {
//      final var serviceDto = selectService(serviceDtoList);
//      final var seatDtoList = selectSeats(serviceDto);
//      serviceSeatMap.put(serviceDto.serviceId(), seatDtoList);
//    }
//    makeReservation(yourPassenger.getPassengerEmail(), serviceSeatMap);
    }
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

  private static List<StationDto> getStations() {
    System.out.println("Getting all the available stations.");
    final var getStationsResponse = HTTP_CLIENT.get("/stations");
    if (getStationsResponse.statusCode() != 200) {
      System.out.println("Something went wrong. Please try again! Status code: " + getStationsResponse.statusCode()
          + " Message: " + getStationsResponse.body());
      book(); // Restart booking
      return List.of();
    } else {
      final var allStations = castList(getStationsResponse.body(), StationDto.class);
      if (allStations.isEmpty()) {
        System.out.println("Something went wrong. No stations found. PLease try again!");
        book(); // Restart booking
        return List.of();
      } else {
        System.out.printf(allStations.toString()
            .replace("StationDto", "%n")
            .replace("[", "")
            .replace("]", "")
            .replace("=", ": ")
            + "%n%n");
        return allStations;
      }
    }
  }

  private static <T> List<T> castList(Object obj, Class<T> listType) {
    if (obj instanceof List<?> list) {
      if (list.stream().allMatch(listType::isInstance)) {
        return list.stream().map(listType::cast).toList();
      }
    }
    throw new ClassCastException(obj.getClass().getName() + " is not a list of " + listType.getName());
  }


  //
//  public static void makeReservation(String passengerEmail, Map<String, List<SeatDto>> seatMap) {
//    final var postBookingResultResponse =
//        (ResponseDto) HTTP_CLIENT.post("/reservation", new BookingRequestDto(passengerEmail, seatMap));
//    if (postBookingResultResponse.getStatusCode() != 201) {
//      System.out.println("An error occured. Status code: " + postBookingResultResponse.getStatusCode()
//          + " Message: " + postBookingResultResponse.getBody());
//    } else {
//      System.out.printf("Status code: %s, Booking successful! %n%s %n%n",
//          postBookingResultResponse.getStatusCode(), postBookingResultResponse.getBody());
//    }
//  }
//
  private static List<RouteDto> getRoutes(List<StationDto> stations) {
    final var departureStationId = setDepartureStation(stations);
    final var arrivalStationId = setArrivalStation(stations, departureStationId);
    final var getRoutesResponse = HTTP_CLIENT.get("/routes/" + departureStationId + "/" + arrivalStationId);
    if (getRoutesResponse.statusCode() != 200) {
      System.out.println("Something went wrong. Please try again! Status code: " + getRoutesResponse.statusCode()
          + " Message: " + getRoutesResponse.body());
      book(); // Restart booking
      return List.of();
    }
    else {
      final var allRoutes = castList(getRoutesResponse.body(), RouteDto.class);
      if (allRoutes.isEmpty()) {
        System.out.println("Something went wrong. No routes found. PLease try again!");
        book(); // Restart booking
        return List.of();
      } else {
        System.out.printf(allRoutes.toString()
            .replace("RouteDto", "%n")
            .replace("stationDtoList", "stationList")
            .replace("StationDto", "")
            .replace("[", "")
            .replace("]", "")
            .replace("=", ": ")
            + "%n%n");
        return allRoutes;
      }
    }
  }

  private static String setDepartureStation(List<StationDto> stations) {
    while (true) {
      System.out.println("Please enter the station ID of your departure station.");
      final var departureStationId = SCANNER.nextLine();
      final var optionalStation = stations.stream().filter(station -> station.stationId().equals(departureStationId))
          .findFirst();
      if (optionalStation.isEmpty()) {
        System.out.println("Try again, please enter a valid station ID.");
      } else {
        return optionalStation.get().stationId();
      }
    }
  }

  private static String setArrivalStation(List<StationDto> stations, String departureStationId) {
    while (true) {
      System.out.println("Please enter the station ID of your arrival station.");
      final var arrivalStationId = SCANNER.nextLine();
      final var optionalStation = stations.stream().filter(station -> station.stationId().equals(arrivalStationId)).findFirst();
      if (optionalStation.isEmpty() || optionalStation.get().stationId().equals(departureStationId)) {
        System.out.println("Try again, please enter a valid station ID.");
      } else {
        return optionalStation.get().stationId();
      }
    }
  }

  private static RouteDto setRoute(List<RouteDto> routes) {
    while (true) {
      System.out.println("Please enter the route ID of your preferred route.");
      final var routeId = SCANNER.nextLine();
      final var optionalRoute = routes.stream().filter(route -> route.routeId().equals(routeId)).findFirst();
      if (optionalRoute.isEmpty()) {
        System.out.println("Try again, please enter a valid route ID.");
      } else {
        return optionalRoute.get();
      }
    }
  }

  private static LocalDateTime setDepartureDateTime() {
    final var LocalDate = setDepartureDate();
    final var LocalTime = setDepartureTime();
    return LocalDateTime.of(LocalDate, LocalTime);
  }

  private static LocalDate setDepartureDate() {
    LocalDate departureDate;
    while (true) {
      try {
        System.out.println("Please set your departure date in this format: 'dd-mm-yyyy'."
            + "The reservation system has train services from today to 30 days in to the future.");
        final var date = SCANNER.nextLine();
        departureDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        break;
      } catch (DateTimeParseException e) {
        System.out.println("Please enter a valid date in format: dd-mm-yyyy.");
      }
    }
    return departureDate;
  }

  private static LocalTime setDepartureTime() {
    LocalTime departureTime;
    while (true) {
      try {
        System.out.println("Please set your departure time in this format: 'hh:mm', 24 hour format no AM/PM."
            + "The reservation system has train services from today to 30 days in to the future.");
        final var time = SCANNER.nextLine();
        departureTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        break;
      } catch (DateTimeParseException e) {
        System.out.println("Please enter a valid date in format: 'HH:mm', 24 hour format no AM/PM.");
      }
    }
    return departureTime;
  }


  //
//  private static List<TrainServiceDto> getServices(int segment, StationDto stationDto) {
//    System.out.printf("Booking service %s out of %s services needed.%n", segment, stationDto.amountOfServicesNeeded());
//    System.out.printf("Getting all the available services on segment %s on route %s.%n", segment, stationDto.routeId());
//    final var getServicesResponse = (ResponseDto) HTTP_CLIENT.get("/services/" + stationDto.routeId() + "/" + segment);
//    if (getServicesResponse.getStatusCode() != 200) {
//      System.out.println("Something went wrong. Please try again. Status code: " + getServicesResponse.getStatusCode()
//          + " Message: " + getServicesResponse.getBody());
//      book();
//      return List.of();
//    } else {
//      @SuppressWarnings("unchecked") final List<TrainServiceDto> trainServiceDtoList = (List<TrainServiceDto>) getServicesResponse.getBody();
//      if (trainServiceDtoList.isEmpty()) {
//        System.out.println("Something went wrong. No Services found. Terminating reservation system!");
//        System.exit(-1);
//        return List.of();
//      } else {
//        System.out.println(trainServiceDtoList);
//        return trainServiceDtoList;
//      }
//    }
//  }
//
//  private static TrainServiceDto selectService(List<TrainServiceDto> services) {
//    while (true) {
//      System.out.println("Please enter the service id you would like to book, example 'JA1-4321'.");
//      final var serviceId = SCANNER.nextLine();
//      final var optionalServiceDto = services.stream().filter(service -> service.serviceId().equals(serviceId)).findFirst();
//      if (optionalServiceDto.isEmpty()) {
//        System.out.println("Try again, please enter a valid service ID.");
//      } else {
//        return optionalServiceDto.get();
//      }
//    }
//  }
//
//  private static List<SeatDto> selectSeats(TrainServiceDto trainServiceDto) {
//    getSeats(trainServiceDto.serviceId());
//    final var amountOfSeats = setAmountOfSeats();
//    final var seatDtoList = new ArrayList<SeatDto>();
//    createSeatList(seatDtoList, amountOfSeats);
//    return seatDtoList;
//  }
//
//  private static void getSeats(String serviceId) {
//    System.out.println("Getting available seats for service " + serviceId);
//    final var getSeatsResponse = (ResponseDto) HTTP_CLIENT.get("/seats/" + serviceId);
//    if (getSeatsResponse.getStatusCode() != 200) {
//      System.out.println("Something went wrong. Please try again. Status code: " + getSeatsResponse.getStatusCode()
//          + " Message: " + getSeatsResponse.getBody());
//      book();
//    } else {
//      @SuppressWarnings("unchecked") final var carriageSeatMap = (Map<String, Map<String, Set<Integer>>>) getSeatsResponse.getBody();
//      if (carriageSeatMap.isEmpty()) {
//        System.out.println("Something went wrong. No Services found. Terminating reservation system!");
//        System.exit(-1);
//      } else {
//        System.out.println(carriageSeatMap);
//      }
//    }
//  }
//
//  private static int setAmountOfSeats() {
//    int amountOfSeats;
//    while (true) {
//      try {
//        System.out.println("Please enter the amount of seats you want to book, example '2'.");
//        amountOfSeats = Integer.parseInt(SCANNER.nextLine());
//        break;
//      } catch (NumberFormatException e) {
//        System.out.println("Invalid input. Please enter a valid integer.");
//      }
//    }
//    return amountOfSeats;
//  }
//
//  private static void createSeatList(List<SeatDto> seats, int amountOfSeats) {
//    for (int j = 0; j < amountOfSeats; j++) {
//      System.out.printf("Booking seat %s out of %s.%n", j + 1, amountOfSeats);
//      while (true) {
//        System.out.println("Please enter the seat number you would like to book, example 'B10'.");
//        final var seatString = SCANNER.nextLine();
//        if (!seatString.matches("^[A-Z]\\d+$")) {
//          System.out.println("Invalid seat format. Expected format: <CarriageID><SeatNumber> (e.g., 'A12').");
//          continue;
//        }
//        seats.add(SeatDto.fromString(seatString));
//        break;
//      }
//    }
//  }
//
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
