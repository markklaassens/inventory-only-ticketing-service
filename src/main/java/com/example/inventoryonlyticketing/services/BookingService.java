package com.example.inventoryonlyticketing.services;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.bookings;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.passengers;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.routes;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.services;
import static com.example.inventoryonlyticketing.utilities.ReservationSystem.tickets;

import com.example.inventoryonlyticketing.dto.BookingDto;
import com.example.inventoryonlyticketing.dto.BookingRequestDto;
import com.example.inventoryonlyticketing.dto.RouteDto;
import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.dto.ServiceDto;
import com.example.inventoryonlyticketing.dto.TicketDto;
import com.example.inventoryonlyticketing.entities.Booking;
import com.example.inventoryonlyticketing.entities.Passenger;
import com.example.inventoryonlyticketing.entities.Seat;
import com.example.inventoryonlyticketing.entities.Service;
import com.example.inventoryonlyticketing.entities.ServiceCarriage;
import com.example.inventoryonlyticketing.entities.Ticket;
import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import com.example.inventoryonlyticketing.exceptions.CarriageNotFoundException;
import com.example.inventoryonlyticketing.exceptions.PassengerNotFoundException;
import com.example.inventoryonlyticketing.exceptions.RouteNotFoundException;
import com.example.inventoryonlyticketing.exceptions.ServiceNotFoundException;
import com.example.inventoryonlyticketing.exceptions.UnavailableSeatsException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingService {

  public List<RouteDto> getAllRoutes() {
    return routes.values().stream().map(RouteDto::from).toList();
  }

  public List<ServiceDto> getAllServicesOnRouteSegment(String routeId, int segment) {
    final var route = Optional.ofNullable(routes.get(routeId)).orElseThrow(() ->
        new RouteNotFoundException("Route with routeId " + routeId + " not found.%n"));
    return route.getSegmentServicesMap().get(segment).stream().map(ServiceDto::from).collect(Collectors.toList());
  }

  public Map<String, Map<String, Set<Integer>>> getAvailableSeatsOnService(String serviceId) {
    final var availableSeatMap = new LinkedHashMap<String, Map<String, Set<Integer>>>();
    final var service = getService(serviceId);

    final var firstClassSeatMap = createSeatMap(service, CarriageType.FIRST_CLASS);
    final var secondClassSeatMap = createSeatMap(service, CarriageType.SECOND_CLASS);

    availableSeatMap.put(CarriageType.FIRST_CLASS.getDisplayName(), firstClassSeatMap);
    availableSeatMap.put(CarriageType.SECOND_CLASS.getDisplayName(), secondClassSeatMap);
    return availableSeatMap;
  }

  private Service getService(String serviceId) {
    return Optional.ofNullable(services.get(serviceId)).orElseThrow(
        () -> new ServiceNotFoundException("Service %s not found.%n".formatted(serviceId)));
  }

  private Map<String, Set<Integer>> createSeatMap(Service service, CarriageType carriageType) {
    final var seatMap = new HashMap<String, Set<Integer>>();
    service.getServiceCarriages().stream()
        .filter(serviceCarriage -> serviceCarriage.getServiceCarriageType().equals(carriageType))
        .forEach(
            serviceCarriage -> seatMap.put(serviceCarriage.getServiceCarriageLabel(), serviceCarriage.getSeatNumbers()));
    return seatMap;
  }

  public BookingDto makeBooking(BookingRequestDto bookingRequestDto) {
    final var passenger = findPassenger(bookingRequestDto.passengerEmail());
    final var serviceIds = bookingRequestDto.serviceSeatMap().keySet();
    final var unavailableSeatsMap = new HashMap<Service, Set<SeatDto>>();
    final var bookingTicketSet = new HashSet<Ticket>();

    // Reserve seats for each service
    for (final var serviceId : serviceIds) {
      final var service = getService(serviceId);
      final var seats = bookingRequestDto.serviceSeatMap().get(serviceId);
      reserveSeatOnService(service, passenger, seats, bookingTicketSet, unavailableSeatsMap);
    }

    // If there are any unavailable seats, clear the booking and throw an exception
    if (!unavailableSeatsMap.entrySet().isEmpty()) {
      resetBooking(bookingTicketSet, unavailableSeatsMap);
    }

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
      Service service,
      Passenger passenger,
      List<SeatDto> seats,
      Set<Ticket> bookingTickets,
      Map<Service, Set<SeatDto>> unavailableSeatsMap
  ) {
    final var unavailableSeatSet = new HashSet<SeatDto>();
    for (SeatDto seatDto : seats) {
      final var serviceCarriage = getServiceCarriage(service, seatDto);
      final var carriageType = serviceCarriage.getServiceCarriageType();
      // Check if seat is available and remove if that's the case, returns false when seat is not available
      final boolean available = serviceCarriage.getSeatNumbers().remove(seatDto.seatNumber());
      if (!available) {
        unavailableSeatSet.add(seatDto);
      } else {
        // Create ticket when seat is available
        final var ticket = new Ticket(
            service,
            passenger,
            carriageType,
            new Seat(seatDto.carriageId(), seatDto.seatNumber()),
            service.getDepatureStation(),
            service.getDepartureTime(),
            service.getArrivalStation(),
            service.getArrivalTime()
        );
        bookingTickets.add(ticket);
      }
    }
    // Save unavailable seats for future processing
    if (!unavailableSeatSet.isEmpty()) {
      unavailableSeatsMap.put(service, unavailableSeatSet);
    }
  }

  private ServiceCarriage getServiceCarriage(Service service, SeatDto seatDto) {
    return service.getServiceCarriages().stream()
        .filter(sc -> sc.getServiceCarriageLabel().equals(seatDto.carriageId()))
        .findFirst()
        .orElseThrow(() -> new CarriageNotFoundException("Carriage %s not found.%n".formatted(seatDto.carriageId())));
  }

  private void resetBooking(Set<Ticket> bookingTicketSet, Map<Service, Set<SeatDto>> unavailableSeatsMap) {
    for (final var ticket : bookingTicketSet) {
      services.get(ticket.getService().getServiceId()).getServiceCarriages()
          .stream()
          .filter(sc -> sc.getServiceCarriageLabel().equals(ticket.getSeat().getServiceCarriageId()))
          .findFirst().orElseThrow(
              () -> new RuntimeException(
                  "Service carriage with ID %s not found.%n".formatted(ticket.getSeat().getServiceCarriageId())))
          .getSeatNumbers().add(ticket.getSeat().getSeatNumber());
    }
    bookingTicketSet.clear();
    throw new UnavailableSeatsException(createExceptionMessage(unavailableSeatsMap));
  }

  private String createExceptionMessage(Map<Service, Set<SeatDto>> unavailableSeats) {
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
      ticket.getService().increasePassengerTotal();
      passenger.getTickets().add(ticket);
      ticket.getSeat().setPassenger(passenger);
      ticket.getDepartureStation().increaseDepartingPassengers();
      ticket.getArrivalStation().increaseArrivingPassengers();
      ticket.setBooking(booking);
    }
    return booking;
  }
}
