import { useEffect, useState } from "react";

type Flight = {
  id: string;
  flightNumber: string;
  departureAirport: string;
  arrivalAirport: string;
  aircraftType: string;
  assignedAircraftId: string;
  status: "DISRUPTED" | "RECOVERED";
  disruptionReason: string | null;
};

type RecoveryOption = {
  aircraftId: string;
  aircraftType: string;
  currentAirport: string;
  requiresRepositioning: boolean;
  rank: number;
};

type ApiError = {
  message?: string;
};

const API_URL = "/api/flights";

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  let response: Response;

  try {
    response = await fetch(url, options);
  } catch {
    throw new Error("Cannot reach the API. Start the backend with `mvn spring-boot:run`.");
  }

  const body = await response.text();
  let data: unknown;

  try {
    data = JSON.parse(body);
  } catch {
    throw new Error("The API returned an invalid response. Check that the backend is running.");
  }

  if (!response.ok) {
    const apiError = data as ApiError;
    throw new Error(apiError.message ?? `Request failed (${response.status}).`);
  }

  return data as T;
}

function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : "Something went wrong.";
}

function App() {
  const [flight, setFlight] = useState<Flight | null>(null);
  const [options, setOptions] = useState<RecoveryOption[]>([]);
  const [selectedAircraftId, setSelectedAircraftId] = useState("");
  const [loading, setLoading] = useState(true);
  const [assigning, setAssigning] = useState(false);
  const [message, setMessage] = useState("");

  async function loadFlight() {
    setLoading(true);
    setMessage("");

    try {
      const flights = await request<Flight[]>(API_URL);
      const demoFlight = flights[0];
      setFlight(demoFlight ?? null);
      setOptions([]);
      setSelectedAircraftId("");

      if (demoFlight?.status === "DISRUPTED") {
        const recoveryOptions = await request<RecoveryOption[]>(
          `${API_URL}/${demoFlight.id}/recovery-options`,
        );
        setOptions(recoveryOptions);
      }
    } catch (error) {
      setFlight(null);
      setMessage(errorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadFlight();
  }, []);

  async function assignAircraft() {
    if (!flight || !selectedAircraftId) {
      return;
    }

    setAssigning(true);
    setMessage("");

    try {
      const recoveredFlight = await request<Flight>(
        `${API_URL}/${flight.id}/reassign`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ aircraftId: selectedAircraftId }),
        },
      );
      setFlight(recoveredFlight);
      setMessage(`Flight recovered with aircraft ${recoveredFlight.assignedAircraftId}.`);
    } catch (error) {
      setMessage(errorMessage(error));
    } finally {
      setAssigning(false);
    }
  }

  return (
    <main className="app">
      <header>
        <p className="eyebrow">FLIGHT OPERATIONS · DEMO</p>
        <h1>Disruption recovery</h1>
        <p className="intro">Review the disrupted flight and assign a compatible replacement aircraft.</p>
      </header>

      {loading && <p>Loading flight…</p>}

      {!loading && message && (
        <div className="message" role="status">
          <p>{message}</p>
          {!flight && <button onClick={() => void loadFlight()}>Retry</button>}
        </div>
      )}

      {!loading && flight && (
        <>
          <section className="card">
            <div className="flight-heading">
              <h2>{flight.flightNumber}</h2>
              <span className={`status ${flight.status.toLowerCase()}`}>{flight.status}</span>
            </div>
            <p className="route">
              {flight.departureAirport} <span aria-hidden="true">→</span> {flight.arrivalAirport}
            </p>
            <dl>
              <div><dt>Aircraft type</dt><dd>{flight.aircraftType}</dd></div>
              <div><dt>Assigned aircraft</dt><dd>{flight.assignedAircraftId}</dd></div>
              {flight.disruptionReason && (
                <div><dt>Reason</dt><dd>{flight.disruptionReason}</dd></div>
              )}
            </dl>
          </section>

          {flight.status === "DISRUPTED" ? (
            <section className="recovery">
              <h2>Recovery options</h2>
              {options.length === 0 ? (
                <p>No compatible aircraft are available.</p>
              ) : (
                <>
                  <div className="options">
                    {options.map((option) => (
                      <label className="option" key={option.aircraftId}>
                        <input
                          type="radio"
                          name="aircraft"
                          value={option.aircraftId}
                          checked={selectedAircraftId === option.aircraftId}
                          onChange={() => setSelectedAircraftId(option.aircraftId)}
                        />
                        <span>
                          <strong>{option.rank}. {option.aircraftId}</strong>
                          {" — "}{option.aircraftType} at {option.currentAirport}
                          <small>
                            {option.requiresRepositioning
                              ? "Repositioning required"
                              : "Already at departure airport"}
                          </small>
                        </span>
                      </label>
                    ))}
                  </div>
                  <button
                    className="assign-button"
                    onClick={assignAircraft}
                    disabled={!selectedAircraftId || assigning}
                  >
                    {assigning ? "Assigning…" : "Assign selected aircraft"}
                  </button>
                </>
              )}
            </section>
          ) : (
            <p className="message success">Recovery complete. The flight is ready for follow-up.</p>
          )}
        </>
      )}

      <footer>Portfolio demo only · Sample data · Not for real-world dispatch</footer>
    </main>
  );
}

export default App;
