package com.example.inventoryonlyticketing.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.inventoryonlyticketing.entities.enums.CarriageType;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceTest {

  private Station departureStation;
  private Station arrivalStation;
  private Set<Carriage> carriages;
  private ZonedDateTime departureTime;
  private ZonedDateTime arrivalTime;
  private Station fictionalStation;
  private Route routeLondonParis;

  @BeforeEach
  public void setUp() {
    departureStation = new Station("london001", "Londown Waterloo");
    arrivalStation = new Station("paris001", "Paris Garde du Nord");
    carriages = new HashSet<>();
    carriages.add(new Carriage("A1234", CarriageType.FIRST_CLASS, 20));
    carriages.add(new Carriage("B4321", CarriageType.SECOND_CLASS, 20));
    departureTime = ZonedDateTime.now();
    arrivalTime = ZonedDateTime.now().plusHours(2);
    fictionalStation = new Station("hogwarts9.75", "Hogsmeade Station");
    routeLondonParis = new Route("LP", List.of(departureStation, arrivalStation));
  }

  @Test
  public void shouldConstructServiceOnValidInput() {
    Service service = new Service("S1-5160", 5160, departureStation, arrivalStation, carriages, departureTime, arrivalTime);
    assertNotNull(service);
    assertEquals("S1-5160", service.getServiceId());
    assertEquals(5160, service.getServiceNumber());
    assertEquals(departureStation, service.getDepatureStation());
    assertEquals(arrivalStation, service.getArrivalStation());
    assertEquals(departureTime, service.getDepartureTime());
    assertEquals(arrivalTime, service.getArrivalTime());
    assertEquals(2, service.getServiceCarriages().size());
  }

  @Test
  public void shouldCreateServiceCarriages() {
    final var service = new Service("S1-5160", 5160, departureStation, arrivalStation, carriages, departureTime,
        arrivalTime);
    final var serviceCarriages = service.getServiceCarriages();

    assertEquals(2, serviceCarriages.size());
    assertTrue(serviceCarriages.stream().anyMatch(sc -> "A".equals(sc.getServiceCarriageLabel())));
    assertTrue(serviceCarriages.stream().anyMatch(sc -> "B".equals(sc.getServiceCarriageLabel())));
  }

  @Test
  public void shouldAddServiceToRoutesAsSegment() {
    final var service = new Service("S001", 101, departureStation, arrivalStation, carriages, departureTime, arrivalTime);

    assertTrue(service.getAssociatedRoutes().contains(routeLondonParis));
    assertTrue(routeLondonParis.getSegmentServicesMap().containsKey(1));
    assertTrue(routeLondonParis.getSegmentServicesMap().get(1).contains(service));
  }

  @Test
  public void shouldThrowNoSuchElementExceptionWhenNoCorrespondingRouteIsFound() {
    assertThrows(NoSuchElementException.class, () ->
        new Service("S1-5160", 5160, departureStation, fictionalStation, carriages, departureTime, arrivalTime));
  }
}
