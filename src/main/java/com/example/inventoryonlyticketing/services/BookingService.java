package com.example.inventoryonlyticketing.services;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.bookings;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stationGrid;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stations;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.tickets;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.trainServices;

import com.example.inventoryonlyticketing.dto.BookingDto;
import com.example.inventoryonlyticketing.dto.BookingRequestDto;
import com.example.inventoryonlyticketing.dto.RouteDto;
import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.dto.StationDto;
import com.example.inventoryonlyticketing.dto.TicketDto;
import com.example.inventoryonlyticketing.entities.Booking;
import com.example.inventoryonlyticketing.entities.Passenger;
import com.example.inventoryonlyticketing.entities.TrainServiceCarriage;
import com.example.inventoryonlyticketing.entities.Station;
import com.example.inventoryonlyticketing.entities.Ticket;
import com.example.inventoryonlyticketing.entities.TrainService;
import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import com.example.inventoryonlyticketing.exceptions.CarriageNotFoundException;
import com.example.inventoryonlyticketing.exceptions.PassengerNotFoundException;
import com.example.inventoryonlyticketing.exceptions.ServiceNotFoundException;
import com.example.inventoryonlyticketing.exceptions.StationNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {

  public List<StationDto> getStations() {
    List<StationDto> stationDtoList = new ArrayList<>();
    for (final var station : stations.values()) {
      final var stationDto = StationDto.from(station);
      stationDtoList.add(stationDto);
    }
    return stationDtoList.stream().sorted(Comparator.comparing(StationDto::stationName)).toList();
  }

  public List<RouteDto> getAllRoutesBetweenStations(String fromStationId, String toStationId) {
    final var fromStation = getStation(fromStationId);
    final var toStation = getStation(toStationId);
    boolean foundCompletedRoute = false;
    Queue<List<Station>> uncompletedRoutes = new LinkedList<>();
    final var completedRoutes = new LinkedHashSet<List<Station>>();
    final var initialRoute = new ArrayList<Station>();
    initialRoute.add(fromStation);
    uncompletedRoutes.add(initialRoute);

    while (!uncompletedRoutes.isEmpty()) {
      final var currentRoute = uncompletedRoutes.poll();
      final var currentStation = currentRoute.getLast();
      if (!foundCompletedRoute || currentRoute.size() <= completedRoutes.getFirst().size()) {
        for (final var neighbourStation : stationGrid.get(currentStation)) {
          if (!currentRoute.contains(neighbourStation)) {
            if (neighbourStation.equals(toStation)) {
              final var completedRoute = new ArrayList<>(currentRoute);
              completedRoute.add(neighbourStation);
              completedRoutes.add(completedRoute);
              foundCompletedRoute = true;
            } else {
              final var uncompletedRoute = new ArrayList<>(currentRoute);
              uncompletedRoute.add(neighbourStation);
              uncompletedRoutes.add(uncompletedRoute);
            }
          }
        }
      }
    }
    final var counter = new AtomicInteger(1);
    return completedRoutes.stream()
        .map(stationList -> stationList.stream()
            .map(StationDto::from)
            .toList())
        .sorted(Comparator.comparingInt(List::size))
        .map(stationList -> RouteDto.from(counter.getAndIncrement(), stationList))
        .toList();
  }

  private Station getStation(String stationId) {
    return Optional.ofNullable(stations.get(stationId)).orElseThrow(() ->
        new StationNotFoundException("Station with station ID " + stationId + " not found.%n"));
  }

//  public List<ServiceDto> getAllServicesOnRouteSegment(String routeId, int segment) {
//    final var route = Optional.ofNullable(routes.get(routeId)).orElseThrow(() ->
//        new RouteNotFoundException("Route with routeId " + routeId + " not found.%n"));
//    return route.getSegmentServicesMap().get(segment).stream().map(ServiceDto::from).collect(Collectors.toList());
//  }

  public Map<String, Map<String, Set<Integer>>> getAvailableSeatsOnService(String serviceId) {
    final var availableSeatMap = new LinkedHashMap<String, Map<String, Set<Integer>>>();
    final var service = getService(serviceId);

    final var firstClassSeatMap = createSeatMap(service, CarriageType.FIRST_CLASS);
    final var secondClassSeatMap = createSeatMap(service, CarriageType.SECOND_CLASS);

    availableSeatMap.put(CarriageType.FIRST_CLASS.getDisplayName(), firstClassSeatMap);
    availableSeatMap.put(CarriageType.SECOND_CLASS.getDisplayName(), secondClassSeatMap);
    return availableSeatMap;
  }

  private TrainService getService(String serviceId) {
    return Optional.ofNullable(trainServices.get(serviceId)).orElseThrow(
        () -> new ServiceNotFoundException("Service %s not found.%n".formatted(serviceId)));
  }

  private Map<String, Set<Integer>> createSeatMap(TrainService trainService, CarriageType carriageType) {
    final var seatMap = new HashMap<String, Set<Integer>>();
    trainService.getServiceCarriages().stream()
        .filter(trainServiceCarriage -> trainServiceCarriage.getTrainServiceCarriageType().equals(carriageType))
        .forEach(
            trainServiceCarriage -> seatMap.put(trainServiceCarriage.getTrainServiceCarriageLabel(), trainServiceCarriage.getSeatNumbers()));
    return seatMap;
  }

  public BookingDto makeBooking(BookingRequestDto bookingRequestDto) {
    final var passenger = findPassenger(bookingRequestDto.passengerEmail());
    final var serviceIds = bookingRequestDto.serviceSeatMap().keySet();
    final var unavailableSeatsMap = new HashMap<TrainService, Set<SeatDto>>();
    final var bookingTicketSet = new HashSet<Ticket>();

    // Reserve seats for each service
    for (final var serviceId : serviceIds) {
      final var service = getService(serviceId);
      final var seats = bookingRequestDto.serviceSeatMap().get(serviceId);
      reserveSeatOnService(service, passenger, seats, bookingTicketSet, unavailableSeatsMap);
    }

    // If there are any unavailable seats, clear the booking and throw an exception
//    if (!unavailableSeatsMap.entrySet().isEmpty()) {
//      resetBooking(bookingTicketSet, unavailableSeatsMap);
//    }

    final var booking = storeBooking(bookingTicketSet, passenger);

    // Convert Tickets to TicketDto
    final var ticketDtoList = bookingTicketSet.stream().map(TicketDto::from).toList();
    return new BookingDto(booking.getBookingId().toString(), passenger.getPassengerEmail(), ticketDtoList);
  }

  private Passenger findPassenger(String passengerEmail) {
    return Optional.ofNullable(passengers.get(passengerEmail)).orElseThrow(
        () -> new PassengerNotFoundException("Passenger %s not found.%n".formatted(passengerEmail)));
  }

  private void reserveSeatOnService(
      TrainService service,
      Passenger passenger,
      List<SeatDto> seats,
      Set<Ticket> bookingTickets,
      Map<TrainService, Set<SeatDto>> unavailableSeatsMap
  ) {
    final var unavailableSeatSet = new HashSet<SeatDto>();
    for (SeatDto seatDto : seats) {
      final var serviceCarriage = getServiceCarriage(service, seatDto);
      final var carriageType = serviceCarriage.getTrainServiceCarriageType();
      // Check if seat is available and remove if that's the case, returns false when seat is not available
      final boolean available = serviceCarriage.getSeatNumbers().remove(seatDto.seatNumber());
      if (!available) {
        unavailableSeatSet.add(seatDto);
      } else {
        // Create ticket when seat is available
//        final var ticket = new Ticket(
//            service,
//            passenger,
//            carriageType,
//            new Seat(seatDto.carriageId(), seatDto.seatNumber()),
//            service.getDepatureStation(),
//            service.getDepartureTime(),
//            service.getArrivalStation(),
//            service.getArrivalTime()
//        );
//        bookingTickets.add(ticket);
      }
    }
    // Save unavailable seats for future processing
    if (!unavailableSeatSet.isEmpty()) {
      unavailableSeatsMap.put(service, unavailableSeatSet);
    }
  }

  private TrainServiceCarriage getServiceCarriage(TrainService trainService, SeatDto seatDto) {
    return trainService.getServiceCarriages().stream()
        .filter(sc -> sc.getTrainServiceCarriageLabel().equals(seatDto.carriageId()))
        .findFirst()
        .orElseThrow(() -> new CarriageNotFoundException("Carriage %s not found.%n".formatted(seatDto.carriageId())));
  }

//  private void resetBooking(Set<Ticket> bookingTicketSet, Map<TrainService, Set<SeatDto>> unavailableSeatsMap) {
//    for (final var ticket : bookingTicketSet) {
//      trainServices.get(ticket.getService().getServiceId()).getServiceCarriages()
//          .stream()
//          .filter(sc -> sc.getServiceCarriageLabel().equals(ticket.getSeat().getServiceCarriageId()))
//          .findFirst().orElseThrow(
//              () -> new RuntimeException(
//                  "Service carriage with ID %s not found.%n".formatted(ticket.getSeat().getServiceCarriageId())))
//          .getSeatNumbers().add(ticket.getSeat().getSeatNumber());
//    }
//    bookingTicketSet.clear();
//    throw new UnavailableSeatsException(createExceptionMessage(unavailableSeatsMap));
//  }

  private String createExceptionMessage(Map<TrainService, Set<SeatDto>> unavailableSeats) {
    final var detailedMessage = new StringBuilder();
    for (final var service : unavailableSeats.keySet()) {
      unavailableSeats.get(service).forEach(seat ->
          detailedMessage.append("Seat %s%s on service %s is not available.%n"
              .formatted(seat.carriageId(), seat.seatNumber(), service.getServiceId())));
    }
    return "Booking failed!%n%s".formatted(detailedMessage);
  }

  private Booking storeBooking(Set<Ticket> bookingTicketSet, Passenger passenger) {
    final var booking = new Booking(bookingTicketSet, passenger);
    bookings.put(booking.getBookingId(), booking);

    for (final var ticket : bookingTicketSet) {
      tickets.put(ticket.getTicketId(), ticket);
//      ticket.getService().increasePassengerTotal();
      passenger.getTickets().add(ticket);
      ticket.getSeat().setPassenger(passenger);
      ticket.getDepartureStation().increaseDepartingPassengers();
      ticket.getArrivalStation().increaseArrivingPassengers();
      ticket.setBooking(booking);
    }
    return booking;
  }
}
