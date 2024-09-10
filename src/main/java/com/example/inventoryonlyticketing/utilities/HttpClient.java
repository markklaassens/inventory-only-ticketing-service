package com.example.inventoryonlyticketing.utilities;

import com.example.inventoryonlyticketing.dto.BookingRequestDto;
import com.example.inventoryonlyticketing.dto.ResponseDto;
import com.example.inventoryonlyticketing.exceptions.CarriageNotFoundException;
import com.example.inventoryonlyticketing.exceptions.InsufficientSearchParametersException;
import com.example.inventoryonlyticketing.exceptions.PassengerNotFoundException;
import com.example.inventoryonlyticketing.exceptions.RouteNotFoundException;
import com.example.inventoryonlyticketing.exceptions.ServiceNotFoundException;
import com.example.inventoryonlyticketing.exceptions.StationNotFoundException;
import com.example.inventoryonlyticketing.exceptions.TicketNotFoundException;
import com.example.inventoryonlyticketing.exceptions.UnavailableSeatsException;
import com.example.inventoryonlyticketing.services.BookingService;
import com.example.inventoryonlyticketing.services.InfoService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class HttpClient {

  private final BookingService bookingService;
  private final InfoService infoService;

  public HttpClient() {
    this.bookingService = new BookingService();
    this.infoService = new InfoService();
  }

  public ResponseDto post(String url, Object body) {
    try {
      if (url.equals("/reservation")) {
        return handleReservationRequest(body);
      }
      return new ResponseDto(404, "Not Found");
    } catch (UnavailableSeatsException e) {
      return new ResponseDto(400, e.getMessage());
    } catch (CarriageNotFoundException | PassengerNotFoundException | RouteNotFoundException | ServiceNotFoundException |
             StationNotFoundException e) {
      return new ResponseDto(404, e.getMessage());
    } catch (Exception e) {
      return new ResponseDto(500, e.getMessage());
    }
  }

  public ResponseDto get(String url) {
    try {
      if ("/stations".equals(url)) {
        return new ResponseDto(200, bookingService.getStations());
      } else if (url.startsWith("/routes/")) {
        final var stationIds = url.substring("/routes/".length()).split("/");
        return new ResponseDto(200, bookingService.getAllRoutesBetweenStations(stationIds[0], stationIds[1]));
      }
      return new ResponseDto(404, "Not Found");
    } catch (InsufficientSearchParametersException | DateTimeParseException e) {
      return new ResponseDto(400, e.getMessage());
    } catch (CarriageNotFoundException | PassengerNotFoundException | RouteNotFoundException | ServiceNotFoundException |
             StationNotFoundException | TicketNotFoundException e) {
      return new ResponseDto(404, e.getMessage());
    } catch (Exception e) {
      return new ResponseDto(500, e.getMessage());
    }
  }

  private ResponseDto handleReservationRequest(Object body) {
    if (!(body instanceof BookingRequestDto bookingRequest)) {
      return new ResponseDto(400, "Invalid Request");
    }
    return new ResponseDto(201, bookingService.makeBooking(bookingRequest));
  }

  private LocalDate dateParser(String pathVariable) {
    return LocalDate.parse(pathVariable);
  }
}
