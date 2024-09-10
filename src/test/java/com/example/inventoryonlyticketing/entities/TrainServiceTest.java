//package com.example.inventoryonlyticketing.entities;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.example.inventoryonlyticketing.entities.enums.CarriageType;
//import java.time.LocalDateTime;
//
//import java.util.HashSet;
//import java.util.NoSuchElementException;
//import java.util.Set;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class TrainServiceTest {
//
//  private Station departureStation;
//  private Station arrivalStation;
//  private Set<Carriage> carriages;
//  private LocalDateTime departureTime;
//  private LocalDateTime arrivalTime;
//  private Station fictionalStation;
//
//  @BeforeEach
//  public void setUp() {
//    departureStation = new Station("LW01", "Londown Waterloo");
//    arrivalStation = new Station("PGDN01", "Paris Garde du Nord");
//    carriages = new HashSet<>();
//    carriages.add(new Carriage("A1234", CarriageType.FIRST_CLASS, 20));
//    carriages.add(new Carriage("B4321", CarriageType.SECOND_CLASS, 20));
//    departureTime = LocalDateTime.now();
//    arrivalTime = LocalDateTime.now().plusHours(2);
//    fictionalStation = new Station("hogwarts9.75", "Hogsmeade Station");
//  }
//
//  @Test
//  public void shouldConstructServiceOnValidInput() {
//    final var service = new TrainService(5160, "S1-5160", departureStation, arrivalStation, carriages, departureTime, arrivalTime);
//    assertNotNull(service);
//    assertEquals("S1-5160", service.getServiceId());
//    assertEquals(5160, service.getServiceNumber());
//    assertEquals(departureStation, service.getDepatureStation());
//    assertEquals(arrivalStation, service.getArrivalStation());
//    assertEquals(departureTime, service.getDepartureTime());
//    assertEquals(arrivalTime, service.getArrivalTime());
//    assertEquals(2, service.getServiceCarriages().size());
//  }
//
//  @Test
//  public void shouldCreateServiceCarriages() {
//    final var service = new TrainService(5160, "S1-5160", departureStation, arrivalStation, carriages, departureTime,
//        arrivalTime);
//    final var serviceCarriages = service.getServiceCarriages();
//
//    assertEquals(2, serviceCarriages.size());
//    assertTrue(serviceCarriages.stream().anyMatch(sc -> "A".equals(sc.getTrainServiceCarriageLabel())));
//    assertTrue(serviceCarriages.stream().anyMatch(sc -> "B".equals(sc.getTrainServiceCarriageLabel())));
//  }
//
//
//  @Test
//  public void shouldThrowNoSuchElementExceptionWhenNoCorrespondingRouteIsFound() {
//    assertThrows(NoSuchElementException.class, () ->
//        new TrainService(5160, "S1-5160", departureStation, fictionalStation, carriages, departureTime, arrivalTime));
//  }
//}
