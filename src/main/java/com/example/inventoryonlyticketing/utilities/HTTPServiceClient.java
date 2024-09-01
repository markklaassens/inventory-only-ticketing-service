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
import com.example.inventoryonlyticketing.interfaces.HTTPClient;
import com.example.inventoryonlyticketing.interfaces.Response;
import com.example.inventoryonlyticketing.services.BookingService;
import com.example.inventoryonlyticketing.services.InfoService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.jetbrains.annotations.NotNull;

public class HTTPServiceClient implements HTTPClient {

  private final BookingService bookingService;
  private final InfoService infoService;

  public HTTPServiceClient() {
    this.bookingService = new BookingService();
    this.infoService = new InfoService();
  }

  @Override
  @NotNull
  public Response post(@NotNull String url, @NotNull Object body) {
    try {
      if (!url.equals("/reservation")) {
        return new ResponseDto(404, "Not Found");
      }
      return handleReservationRequest(body);
    } catch (UnavailableSeatsException e) {
      return new ResponseDto(400, e.getMessage());
    } catch (CarriageNotFoundException | PassengerNotFoundException | RouteNotFoundException | ServiceNotFoundException |
             StationNotFoundException e) {
      return new ResponseDto(404, e.getMessage());
    } catch (Exception e) {
      return new ResponseDto(500, e.getMessage());
    }
  }

  @Override
  @NotNull
  public Response get(@NotNull String url) {
    try {
      if ("/routes".equals(url)) {
        return new ResponseDto(200, bookingService.getAllRoutes());
      } else if (url.startsWith("/services/") && url.substring("/services/".length()).contains("/")) {
        final var pathVariables = splitUrl(url.substring("/services/".length()));
        final var routeId = pathVariables[0];
        final var routeSegment = Integer.parseInt(pathVariables[1]);
        return new ResponseDto(200, bookingService.getAllServicesOnRouteSegment(routeId, routeSegment));
      } else if (url.startsWith("/services/")) {
        final var serviceId = url.substring("/services/".length());
        return new ResponseDto(200, infoService.getServiceInfo(serviceId));
      } else if (url.startsWith("/seats/")) {
        final var serviceId = url.substring("/seats/".length());
        return new ResponseDto(200, bookingService.getAvailableSeatsOnService(serviceId));
      } else if ("/stations".equals(url)) {
        return new ResponseDto(200, infoService.getPassengersAtStations());
      } else if (url.startsWith("/stations/")) {
        final var pathVariables = splitUrl(url.substring("/stations/".length()));
        if (pathVariables.length == 2) {
          final var departureStationId = pathVariables[0];
          final var arrivalStationId = pathVariables[1];
          return new ResponseDto(200,
              infoService.getAmountOfPassengersBetweenTwoStations(departureStationId, arrivalStationId));
        } else if (pathVariables.length == 4) {
          return new ResponseDto(200, infoService.getPassengerByArrivalStationDateServiceNumberAndSeat(
              pathVariables[0],
              dateParser(pathVariables[1]),
              Integer.parseInt(pathVariables[2]),
              pathVariables[3]
          ));
        }
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

  private LocalDate dateParser(String pathVariable) {
    return LocalDate.parse(pathVariable);
  }

  private Response handleReservationRequest(Object body) {
    if (!(body instanceof BookingRequestDto)) {
      return new ResponseDto(400, "Invalid Request");
    }
    return new ResponseDto(201, bookingService.makeBooking((BookingRequestDto) body));
  }

  private String[] splitUrl(String url) {
    return url.split("/");
  }
}
