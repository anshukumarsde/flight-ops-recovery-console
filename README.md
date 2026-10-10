# Flight Operations Recovery Console

A beginner-friendly portfolio demo for a flight disruption recovery workflow. This is a standalone demo: it is not a NetJets system and does not use proprietary NetJets data.

## What the application currently does

The project is at its first backend milestone:

- Runs a Java 17+ Spring Boot REST API.
- `GET /api/flights` returns a JSON array containing one hard-coded example flight.
- The example flight is marked `DISRUPTED` because of a maintenance hold.
- Includes a Spring MVC test that checks the endpoint returns the example flight.

For now, this is only a backend endpoint. There is no React UI, database, aircraft recovery-options endpoint, or reassignment workflow yet. The sample data is in memory and resets when the application restarts.

The demo illustrates software structure and is not intended for real flight dispatch or safety decisions.

## Requirements

- Java 17 or newer
- Maven 3.6.3 or newer

## Run locally

Open a terminal in the project folder, then run:

```powershell
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. Keep the terminal open while using it.

## Try the API

Request the flight list at `http://localhost:8080/api/flights`, or run this in a separate PowerShell terminal:

```powershell
Invoke-RestMethod http://localhost:8080/api/flights
```

The endpoint returns an array with a flight like this:

```json
[
  {
    "id": "FL-1001",
    "flightNumber": "NJ 1001",
    "departureAirport": "KTEB",
    "arrivalAirport": "KMIA",
    "aircraftType": "Citation Latitude",
    "assignedAircraftId": "N700NJ",
    "status": "DISRUPTED",
    "disruptionReason": "Maintenance hold"
  }
]
```

## Run tests

From the project folder:

```powershell
cd backend
mvn test
```

## Project layout

```text
backend/
  src/main/java/com/example/flightops/
    FlightOpsApplication.java       Spring Boot application entry point
    api/FlightController.java       GET /api/flights endpoint
    model/Flight.java               Flight response data
  src/test/java/com/example/flightops/api/
    FlightControllerTest.java       Endpoint test
```

## Planned next steps

The recovery-options endpoint, compatible-aircraft ranking, reassignment workflow, and React/TypeScript operations UI are not implemented yet. They can be added one small, testable step at a time.
