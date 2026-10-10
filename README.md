# Flight Operations Recovery Console

A beginner-friendly portfolio demo for a flight disruption recovery workflow. This is a standalone demo: it is not a NetJets system and does not use proprietary NetJets data.

## What the application currently does

The project currently has a small backend workflow:

- Runs a Java 17+ Spring Boot REST API.
- `GET /api/flights` returns one hard-coded example flight.
- `GET /api/flights/{id}/recovery-options` returns available aircraft of the same type for a disrupted flight.
- Recovery options at the flight's departure airport are ranked first. Other airports require repositioning.
- `POST /api/flights/{id}/reassign` assigns a selected compatible, available aircraft and marks the flight `RECOVERED`.
- The React/TypeScript operations screen displays the flight, lets an operator choose a recovery option, and submits the reassignment.
- Includes Spring MVC tests for listing flights, recovery-option ranking, reassignment, and invalid choices.

The example flight is marked `DISRUPTED` because of a maintenance hold. Aircraft and flight data are hard-coded in memory and reset when the backend restarts. There is no database.

The demo illustrates software structure and is not intended for real flight dispatch or safety decisions.

## Requirements

- Java 17 or newer
- Maven 3.6.3 or newer
- Node.js 18 or newer (includes npm)

## Run locally

Start the backend in one terminal:

```powershell
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. Keep the terminal open while using it.

In a second terminal, install and start the frontend:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Open the Vite URL printed in the terminal (normally `http://127.0.0.1:5173`). The Vite development server forwards `/api` requests to the backend on port 8080. Keep both terminals open while using the application.

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

Assign an aircraft from the recovery options to recover the flight:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/api/flights/FL-1001/reassign `
  -ContentType "application/json" `
  -Body '{"aircraftId":"N701NJ"}'
```

The response contains the updated flight with status `RECOVERED`. Since the demo stores state in memory, restarting the backend resets the example flight.

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

Build and type-check the frontend:

```powershell
cd frontend
npm.cmd run build
```

## Automated checks

GitHub Actions runs the backend tests and frontend production build on each push and pull request. The checks use Java 17 and Node.js 20.

## Project layout

```text
backend/
  src/main/java/com/example/flightops/
    FlightOpsApplication.java       Spring Boot application entry point
    api/FlightController.java       Flight list and recovery-options endpoints
    api/ReassignRequest.java        Reassignment request body
    model/Flight.java               Flight response data
    model/Aircraft.java             Aircraft response and status
    model/RecoveryOption.java        Ranked recovery-option response
    service/FlightRecoveryService.java Filters and ranks aircraft
  src/test/java/com/example/flightops/api/
    FlightControllerTest.java       Endpoint test
frontend/
  src/App.tsx                       Operations screen and API calls
  src/styles.css                    Responsive screen styles
```

## Planned next steps

The demo uses in-memory sample data. A database and persistent operational state are outside the current MVP.
