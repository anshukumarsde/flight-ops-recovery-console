# Flight Operations Recovery Console

A beginner-friendly portfolio demo for a flight disruption recovery workflow. This is a standalone demo: it is not a NetJets system and does not use proprietary NetJets data.

## What the application currently does

The project currently has a small backend workflow:

- Runs a Java 17+ Spring Boot REST API.
- `GET /api/flights` returns one hard-coded example flight.
- `GET /api/flights/{id}/recovery-options` returns available aircraft of the same type for a disrupted flight.
- Recovery options at the flight's departure airport are ranked first. Other airports require repositioning.
- Includes Spring MVC tests for the flight list, recovery-option ranking, and unknown flight IDs.

The example flight is marked `DISRUPTED` because of a maintenance hold. Aircraft and flight data are hard-coded in memory and reset when the application restarts. There is no React UI, database, or reassignment workflow yet.

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

Request recovery options for the demo flight:

```powershell
Invoke-RestMethod http://localhost:8080/api/flights/FL-1001/recovery-options
```

The response lists `N701NJ` first because it is already at `KTEB`, the flight's departure airport. `N702NJ` is also available and compatible, but requires repositioning from `KOPF`. Aircraft that are in maintenance, already assigned, or a different type are excluded.

The flight-list endpoint returns an array with a flight like this:

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
    api/FlightController.java       Flight list and recovery-options endpoints
    model/Flight.java               Flight response data
    model/Aircraft.java             Aircraft response and status
    model/RecoveryOption.java        Ranked recovery-option response
    service/FlightRecoveryService.java Filters and ranks aircraft
  src/test/java/com/example/flightops/api/
    FlightControllerTest.java       Endpoint test
```

## Planned next steps

The reassignment workflow and React/TypeScript operations UI are not implemented yet. They can be added one small, testable step at a time.
