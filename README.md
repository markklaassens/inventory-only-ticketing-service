# Ticket Service Reservation System

This project is a Java-based ticket reservation system that allows users to book tickets for train services between various European cities. It demonstrates the process of initializing data, running predefined booking scenarios, and providing an interactive interface for making custom bookings.

## Project Structure

- **Main Class (`Main.java`)**:
  The entry point of the application. It initializes data, runs predefined booking scenarios, and provides an interactive loop for the user to make custom bookings.

- **Utilities**:
    - **`UserInterface`**: Handles user interactions, booking scenarios, and retrieving information from the reservation system.
    - **`ReservationSystem`**: Manages core data structures like stations, routes, carriages, passengers, and services.
    - **`DataInitializer`**: Initializes the necessary data (stations, routes, carriages, services, passengers, and bookings) for the reservation system.
    - **`HTTPClient`**: Simulates HTTP client behavior to interact with backend services (`BookingService` and `InfoService`).

## How to Run the Application

1. **Initialize Data**: The application begins by calling `initializeData()` to set up stations, routes, carriages, services, passengers, and pre-defined bookings.
2. **Predefined Booking Scenarios**:
    - `bookParisAmsterdam()`: Books a journey from Paris to Amsterdam twice to demonstrate seat availability and exceptions.
    - `bookLondonAmsterdam()`: Books a journey from London to Amsterdam twice to demonstrate seat availability and exceptions.
    - `getInfo()`: Retrieves and displays information about current reservations and passenger details.
3. **Interactive Booking**:
    - The user is prompted to create custom bookings. The process involves selecting routes, services, and seats.
    - After each booking, the system displays updated information about reservations.

## Unit Testing

The project includes basic unit tests to validate the behavior of two components:

- **`ServiceTest`**: Tests for the `Service` class to ensure that:
    - Services are constructed correctly with valid inputs.
    - Service carriages are created properly.
    - Services are added to routes as expected.
    - Exceptions are thrown correctly for invalid or missing data, such as no departure or arrival stations, or no matching routes.

- **`BookingServiceTest`**: Tests for the `BookingService` class to verify that:
    - All routes and services are retrieved correctly.
    - Available seats for a service are calculated properly.
    - Bookings are created successfully, and exceptions are thrown when conditions such as unavailable seats or missing passengers are encountered.

For a more comprehensive example of unit testing, please refer to my other GitHub project: [Spring Boot Sandbox](https://github.com/markklaassens/spring-boot-sandbox)

## Future Improvements

There are several areas where the project could be enhanced further, as some features have not yet been fully developed. Therefore, certain methods and fields in some classes are currently unused.

1. **Assign passengers to specific seats**: Currently, the name and email address of the passenger booking the tickets are applied to all seats in the booking. In the future, it should be possible to specify a unique name for each seat.
2. **Return only valid services for multi-segment routes**: When booking a multi-segment route, the service currently returns all possible services for each segment. A future enhancement could consider the arrival time of the previous segment to filter and return only valid subsequent services.
3. **Display total distance for routes**: Although each route includes a map showing the distance from the starting point to each stop, there is currently no functionality to display the total distance. This feature could be utilized in the future for more comprehensive route information.
4. **Automatically associate services with routes upon route creation**: Automate the addition of services to a route upon route creation to streamline setup.
5. **Refactor `ServiceCarriage` class to inherit from `Carriage` class**: Modify `ServiceCarriage` to extend `Carriage` for improved code reuse and reduce boilerplate code.
6. **Terminate booking process and return an error when seats are unavailable on a route segment**: Stop the booking process and provide an error message if seats are not available for any segment of the route.
7. **Accommodate more than 26 carriages on a service**: Currently, each `ServiceCarriage` is assigned a letter as its label, but there are only 26 letters in the alphabet, the system needs to be capable of handling services with more than 26 carriages.

If you have any suggestions for improvement or feedback, please feel free to share them with me!

## Troubleshooting

If you have a GIT unrelated history problem with your local main branch, my apologies I've amended edits to the initial commit. This can cause problems if you already have the project cloned locally. The easiest is to check out the origin/main branch and choose drop local commits or remove project locally and clone it again from GitHub.
I understand this isn't the preferred way to work with Git, but for a personal showcase project, it is the quickest and easiest approach for me.