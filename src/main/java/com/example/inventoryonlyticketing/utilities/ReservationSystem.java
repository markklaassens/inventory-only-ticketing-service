package com.example.inventoryonlyticketing.utilities;

import com.example.inventoryonlyticketing.entities.Booking;
import com.example.inventoryonlyticketing.entities.Carriage;
import com.example.inventoryonlyticketing.entities.Passenger;
import com.example.inventoryonlyticketing.entities.Route;
import com.example.inventoryonlyticketing.entities.Service;
import com.example.inventoryonlyticketing.entities.Station;
import com.example.inventoryonlyticketing.entities.Ticket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReservationSystem {

  public static final Map<String, Station> stations = new HashMap<>();
  public static final Map<String, Carriage> carriages = new HashMap<>();
  public static final Map<String, Route> routes = new HashMap<>();
  public static final Map<String, Service> services = new HashMap<>();
  public static final Map<String, Passenger> passengers = new HashMap<>();
  public static final Map<UUID, Booking> bookings = new HashMap<>();
  public static final Map<UUID, Ticket> tickets = new HashMap<>();
}
