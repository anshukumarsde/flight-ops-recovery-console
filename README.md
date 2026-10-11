# Flight Operations Recovery Console

This is a small portfolio demo of one flight-operations task: finding a replacement aircraft when the aircraft assigned to a flight becomes unavailable.

This is an independent demo project. It is not a NetJets system, does not use NetJets data, and must not be used to make real flight or safety decisions.

## What the demo does

The demo starts with one example flight:

- Flight **NJ 1001** is due to fly from **KTEB** to **KMIA**.
- Its assigned aircraft, **N700NJ**, is on a maintenance hold.
- The flight is therefore marked **DISRUPTED**.

The screen asks the backend for replacement choices. The backend shows only aircraft that are both available and the same type as the flight requires. It puts an aircraft already at **KTEB** before one that would need to travel to KTEB.

For this example, the choices are:

1. **N701NJ**, at KTEB, so it does not need to be repositioned.
2. **N702NJ**, at KOPF, so it would need to be repositioned.

Choose an aircraft and select **Assign selected aircraft**. The backend assigns it to the flight and changes the flight status to **RECOVERED**. The screen then shows that recovery is complete.

The app is split into two parts: a Java backend that stores flight, aircraft, and recovery history in a local H2 database and makes the recovery decision, and a React screen in the browser that displays the flight and sends the operator's selection to the backend. Both parts need to be running for the full demo to work.

## What this demo does not do

The flight and aircraft information is example data, not live operational data. H2 stores the app's records locally in `backend/data/flightops.mv.db`, so assignments and completed recovery history remain available after a restart. The recovery rule is intentionally limited to aircraft type, availability, and current airport. It does not check crew, maintenance records beyond the sample hold, weather, airport restrictions, aircraft performance, passenger needs, or any other real dispatch requirements.

## What you need

Install these tools before running the app:

- **Java 17 or newer** to run the backend.
- **Maven** to start and test the backend.
- **Node.js** (which includes npm) to start the browser screen.

After installing a tool, open a new PowerShell window so it can find the tool. You can check that Java and Node.js are available with:

```powershell
java -version
node --version
npm.cmd --version
```

## Run the app

Use two PowerShell windows: one for the backend and one for the frontend. Run each `cd` command from the project folder.

### 1. Start the backend

In the first PowerShell window:

```powershell
cd backend
mvn spring-boot:run
```

Wait until Spring Boot says the application has started. Leave this window open. The backend listens on port **8080**.

### 2. Start the frontend

In a second PowerShell window, return to the project folder first if needed, then run:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

`npm.cmd install` downloads the frontend packages. You only need to do this the first time, or after frontend dependencies change. `npm.cmd run dev` starts the browser screen.

Open the local web address printed in the terminal. It is usually:

```text
http://127.0.0.1:5173
```

Leave both windows open while using the app. If the page says the API cannot be reached, check that the backend is still running in the first window.

## Try the recovery

1. Confirm that the screen shows flight **NJ 1001** as **DISRUPTED**.
2. Compare the replacement choices and their current airports.
3. Select **N701NJ** or **N702NJ**.
4. Select **Assign selected aircraft**.
5. Confirm that the flight changes to **RECOVERED**.
6. Open **History** to see the incident reason, aircraft replaced, recovery aircraft, repositioning requirement, and recovery time.

The flight remains recovered after the backend restarts, and its recovery is kept in **History**. To run the demo workflow again, create a new disrupted flight or reset the local H2 database after stopping the backend.

## Useful links while the backend is running

- `http://localhost:8080/api/flights` shows the example flight data.
- `http://localhost:8080/api/flights/FL-1001/recovery-options` shows the replacement aircraft returned by the backend.
- `http://localhost:8080/api/flights/history` shows completed recovery records.
- `http://localhost:8080/h2-console` opens the H2 console (JDBC URL: `jdbc:h2:file:./data/flightops;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE`, user: `sa`, blank password).
