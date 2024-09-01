package com.example.inventoryonlyticketing;

import static com.example.inventoryonlyticketing.utilities.DataInitializer.initializeBookings;
import static com.example.inventoryonlyticketing.utilities.DataInitializer.initializeData;
import static com.example.inventoryonlyticketing.utilities.UserInterface.book;
import static com.example.inventoryonlyticketing.utilities.UserInterface.bookLondonAmsterdam;
import static com.example.inventoryonlyticketing.utilities.UserInterface.bookParisAmsterdam;
import static com.example.inventoryonlyticketing.utilities.UserInterface.getInfo;
import static com.example.inventoryonlyticketing.utilities.UserInterface.newBooking;

public class Main {

  public static void main(String[] args) {
    try {
      initializeData(); // First we initialize data
      initializeBookings(); // Second we make some pre-made bookings
      bookParisAmsterdam(); // Run scenario to book tickets
      bookParisAmsterdam(); // Run scenario again to prove seats are taken and exception is thrown
      bookLondonAmsterdam(); // Run scenario to book tickets
      bookLondonAmsterdam(); // Run scenario again to prove seats are taken and exception is thrown
      getInfo(); // Get info from reservation system

      boolean makeBooking = true;
      System.out.printf("Welcome to the Reservation System!%n");
      while (makeBooking) {
        book(); // Create your own bookings
        getInfo(); // Get info from reservation system
        makeBooking = newBooking();
      }
    } catch (Exception e) {
      System.out.println("An unexpected error occurred" + e.getMessage());
    }
  }
}