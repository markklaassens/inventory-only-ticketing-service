package com.example.inventoryonlyticketing.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.inventoryonlyticketing.dto.BookingRequestDto;
import com.example.inventoryonlyticketing.dto.SeatDto;
import com.example.inventoryonlyticketing.entities.Carriage;
import com.example.inventoryonlyticketing.entities.Passenger;
import com.example.inventoryonlyticketing.entities.Route;
import com.example.inventoryonlyticketing.entities.Service;
import com.example.inventoryonlyticketing.entities.Station;
import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import com.example.inventoryonlyticketing.exceptions.CarriageNotFoundException;
import com.example.inventoryonlyticketing.exceptions.PassengerNotFoundException;
import com.example.inventoryonlyticketing.exceptions.RouteNotFoundException;
import com.example.inventoryonlyticketing.exceptions.ServiceNotFoundException;
import com.example.inventoryonlyticketing.exceptions.UnavailableSeatsException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

  private BookingService bookingService = new BookingService();
  private Route route;
  private Service service;

  @BeforeEach
  void setup() {
    new Passenger("test@example.com", "Test User");
    final var departureStation = new Station("london001", "London Waterloo");
    final var arrivalStation = new Station("paris001", "Paris Garde du Nord");
    final var carriages = new HashSet<Carriage>();
    carriages.add(new Carriage("A1234", CarriageType.FIRST_CLASS, 20));
    carriages.add(new Carriage("B4321", CarriageType.SECOND_CLASS, 20));
    final var departureTime = ZonedDateTime.now();
    final var arrivalTime = ZonedDateTime.now().plusHours(2);

    route = new Route("LP", List.of(departureStation, arrivalStation));
    service = new Service("S1-5160", 5160, departureStation, arrivalStation, carriages, departureTime, arrivalTime);
    bookingService = new BookingService();
  }

  @Test
  void shouldGetAllRoutes() {
    final var routeList = bookingService.getAllRoutes();
    assertEquals(1, routeList.size());
    Assertions.assertEquals(route.getRouteId(), routeList.getFirst().routeId());
  }

  @Test
  void shouldGetAllServicesOnRouteSegment() {
    final var serviceDtoList = bookingService.getAllServicesOnRouteSegment("LP", 1);
    assertEquals(1, serviceDtoList.size());
    Assertions.assertEquals(service.getServiceId(), serviceDtoList.getFirst().serviceId());
  }

  @Test
  void getAvailableSeatsOnService() {
    final var serviceCarriageSeatMap = bookingService.getAvailableSeatsOnService(service.getServiceId());
    assertEquals(2, serviceCarriageSeatMap.size());
    for (final var serviceCarriageType : serviceCarriageSeatMap.keySet()) {
      assertEquals(1, serviceCarriageSeatMap.get(serviceCarriageType)
          .size()); // Expect one first class and one in second class service carriage.
      for (final var carriage : serviceCarriageSeatMap.get(serviceCarriageType).keySet()) {
        assertEquals(20, serviceCarriageSeatMap.get(serviceCarriageType).get(carriage).size());
      }
    }
  }

  @Test
  void shouldMakeBooking() {
    final var seatDto = new SeatDto("A", 12);
    final var bookingRequestDto = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto)));

    final var bookingDto = bookingService.makeBooking(bookingRequestDto);

    Assertions.assertEquals(bookingRequestDto.passengerEmail(), bookingDto.passengerEmail());
    Assertions.assertEquals(bookingRequestDto.serviceSeatMap().size(), bookingDto.tickets().size());
    Assertions.assertEquals(seatDto.carriageId() + seatDto.seatNumber(), bookingDto.tickets().getFirst().seat());
  }

  @Test
  void shouldResetTicketsOnUnavailableSeats() {
    final var seatDto1 = new SeatDto("A", 11);
    final var seatDto2 = new SeatDto("A", 12);
    final var bookingRequestDto1 = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto1)));
    final var bookingRequestDto2 = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto2, seatDto1)));
    final var bookingRequestDto3 = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto2)));

    bookingService.makeBooking(bookingRequestDto1); // Book seat A11
    assertThrows(UnavailableSeatsException.class,
        () -> bookingService.makeBooking(bookingRequestDto2)); // Try to book seat A12 and seat A11
    final var bookingDto = bookingService.makeBooking(
        bookingRequestDto3); // Book seat A12 again to prove it is still available

    Assertions.assertEquals(bookingRequestDto3.passengerEmail(), bookingDto.passengerEmail());
    Assertions.assertEquals(bookingRequestDto3.serviceSeatMap().size(), bookingDto.tickets().size());
    Assertions.assertEquals(seatDto2.carriageId() + seatDto2.seatNumber(), bookingDto.tickets().getFirst().seat());
  }

  @Test
  void shouldThrowExceptionOnInvalidRoute() {
    assertThrows(RouteNotFoundException.class, () -> bookingService.getAllServicesOnRouteSegment("INVALID", 1));
  }

  @Test
  void shouldThrowExceptionOnInvalidService() {
    assertThrows(ServiceNotFoundException.class, () -> bookingService.getAvailableSeatsOnService("INVALID_SERVICE"));
  }

  @Test
  void shouldThrowExceptionOnUnavailableSeats() {
    final var seatDto = new SeatDto("A", 12);
    final var bookingRequestDto = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto)));
    bookingService.makeBooking(bookingRequestDto);

    assertThrows(UnavailableSeatsException.class, () -> bookingService.makeBooking(bookingRequestDto));
  }

  @Test
  void shouldThrowExceptionWhenPassengerIsNotFound() {
    final var seatDto = new SeatDto("A", 12);
    final var bookingRequestDto = new BookingRequestDto("invalid@example.com", Map.of("S1-5160", List.of(seatDto)));

    assertThrows(PassengerNotFoundException.class, () -> bookingService.makeBooking(bookingRequestDto));
  }

  @Test
  void shouldThrowExceptionWhenCarriageIsNotFound() {
    final var seatDto = new SeatDto("INVALID", 99);
    final var bookingRequestDto = new BookingRequestDto("test@example.com", Map.of("S1-5160", List.of(seatDto)));

    assertThrows(CarriageNotFoundException.class, () -> bookingService.makeBooking(bookingRequestDto));
  }
}
