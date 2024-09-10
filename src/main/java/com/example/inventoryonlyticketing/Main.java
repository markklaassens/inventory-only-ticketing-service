package com.example.inventoryonlyticketing;

import static com.example.inventoryonlyticketing.utilities.DataInitializer.initializeData;
import static com.example.inventoryonlyticketing.utilities.UserInterface.book;
import static com.example.inventoryonlyticketing.utilities.UserInterface.newBooking;

import com.example.inventoryonlyticketing.utilities.HttpClient;

public class Main {


  public static void main(String[] args) {
    try {
      initializeData(); // First we initialize data

//      System.out.printf(httpClient.get("/stations").toString().replace("StationDto", "%nStationDto"));

      boolean makeBooking = true;
      System.out.printf("Welcome to the Reservation System!%n");
      while (makeBooking) {
        book(); // Create your own bookings
//        getInfo(); // Get info from reservation system
        makeBooking = newBooking();
      }
    } catch (Exception e) {
      System.out.println("An unexpected error occurred" + e.getMessage());
    }
  }
}