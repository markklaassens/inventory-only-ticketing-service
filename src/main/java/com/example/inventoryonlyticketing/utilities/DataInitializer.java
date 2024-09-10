package com.example.inventoryonlyticketing.utilities;

import static com.example.inventoryonlyticketing.utilities.ReservationSystem.stationGrid;

import com.example.inventoryonlyticketing.entities.TrainService;
import com.example.inventoryonlyticketing.entities.Station;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DataInitializer {


  public static void initializeData() {
    final var stationGrid = initializeStations();
    initializeServices(stationGrid);
  }

  private static Map<Station, Set<Station>> initializeStations() {

    // French train stations
    final var parisStation = new Station("PGN01", "Paris Gare du Nord");
    final var calaisStation = new Station("CV01", "Calais Ville");
    final var marseilleStation = new Station("MSC01", "Marseille Saint-Charles");
    final var bordeauxStation = new Station("BSJ01", "Bordeaux-Saint-Jean");
    final var annecyStation = new Station("AS01", "Annecy Station");

    // UK train stations
    final var edinburghStation = new Station ("EW01", "Edinburgh Waverley");
    final var londonStation = new Station("LW01", "London Waterloo");

    //Germain train stations
    final var berlinStation = new Station("BH01", "Berlin Hauptbahnhof");
    final var hamburgStation = new Station("HCS01", "Hamburg Central Station");
    final var munchenStation = new Station("MH01", "Munchen Hauptbahnhof");

    // Other train stations
    final var amsterdamStation = new Station("AC01", "Amsterdam Centraal");
    final var brusselStation = new Station("BC01", "Brussel-Centraal");
    final var copenhagenStation = new Station("CCS01", "Copenhagen Central Station");

    stationGrid.put(edinburghStation, Set.of(londonStation));
    stationGrid.put(londonStation, Set.of(edinburghStation, calaisStation));
    stationGrid.put(calaisStation, Set.of(londonStation, parisStation, bordeauxStation));
    stationGrid.put(parisStation, Set.of(calaisStation, amsterdamStation, brusselStation, marseilleStation, bordeauxStation, annecyStation));
    stationGrid.put(bordeauxStation, Set.of(calaisStation, parisStation, marseilleStation));
    stationGrid.put(annecyStation, Set.of(parisStation, marseilleStation));
    stationGrid.put(marseilleStation, Set.of(parisStation, bordeauxStation, annecyStation));
    stationGrid.put(brusselStation, Set.of(parisStation, amsterdamStation, berlinStation, munchenStation));
    stationGrid.put(amsterdamStation, Set.of(parisStation, brusselStation, hamburgStation, berlinStation));
    stationGrid.put(berlinStation, Set.of(brusselStation, amsterdamStation, hamburgStation, munchenStation));
    stationGrid.put(hamburgStation, Set.of(brusselStation, amsterdamStation, copenhagenStation));
    stationGrid.put(munchenStation, Set.of(brusselStation, berlinStation));
    stationGrid.put(copenhagenStation, Set.of(hamburgStation));
    return stationGrid;
  }

  private static void initializeServices(final Map<Station, Set<Station>> stationGrid) {
    try {
      // Generate services on routes for the upcoming 30 days
      for (int i = 0; i <= 30; i++) {
        // Get every station on the station grid
        int stationNumber = 1;
        for (final var station : stationGrid.keySet()) {
          //Get every station it is connected to
          int connectedStationNumber = 1;
          for (final var connectedStation: stationGrid.get(station)) {
            final var date = LocalDate.now().plusDays(i);
            final var localDateString = date.toString().substring(0, 2).toUpperCase();
            final var serviceNumber = 1000 * stationNumber + connectedStationNumber;
            //Generate morning service
            new TrainService(
                serviceNumber,
                localDateString + "-" + serviceNumber,
                station,
                connectedStation,
                20,
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(5, 0, 0).plusHours((stationNumber + connectedStationNumber))
                ),
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(7, 0, 0).plusHours((stationNumber + connectedStationNumber))
                )
            );
            // Generate noon service
            new TrainService(
                serviceNumber,
                localDateString + "-" + serviceNumber,
                station,
                connectedStation,
                20,
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(12, 0, 0).plusHours((stationNumber + connectedStationNumber))
                ),
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(14, 0, 0).plusHours((stationNumber + connectedStationNumber))
                )
            );
            // Generate evening service
            new TrainService(
                serviceNumber,
                localDateString + "-" + serviceNumber,
                station,
                connectedStation,
                20,
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(18, 0, 0).plusHours((stationNumber + connectedStationNumber))
                ),
                LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(20, 0, 0).plusHours((stationNumber + connectedStationNumber))
                )
            );
            connectedStationNumber++;
          }
          stationNumber++;
        }
      }
    } catch (NoSuchElementException | IllegalArgumentException e){
      System.out.printf("Failed to create service: %s.%n".formatted(e.getMessage()));  }
  }
}
