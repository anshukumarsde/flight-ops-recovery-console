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

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : "An unexpected error occurred.";
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, options);
  const data = await response.json();

  if (!response.ok) {
    const error = data as ApiError;
    throw new Error(error.message ?? `Request failed (${response.status})`);
  }

  return data as T;
}

function App() {
  const [flights, setFlights] = useState<Flight[]>([]);
  const [options, setOptions] = useState<RecoveryOption[]>([]);
  const [selectedAircraftId, setSelectedAircraftId] = useState("");
  const [loadingFlights, setLoadingFlights] = useState(true);
  const [loadingOptions, setLoadingOptions] = useState(false);
  const [assigning, setAssigning] = useState(false);
  const [apiConnected, setApiConnected] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const flight = flights[0];

  useEffect(() => {
    request<Flight[]>(API_URL)
      .then((loadedFlights) => {
        setFlights(loadedFlights);
        setApiConnected(true);
      })
      .catch((requestError: unknown) => {
        setApiConnected(false);
        setError(getErrorMessage(requestError));
      })
      .finally(() => setLoadingFlights(false));
  }, []);

  useEffect(() => {
    if (!flight || flight.status !== "DISRUPTED") {
      setOptions([]);
      setSelectedAircraftId("");
      return;
    }

    let isCurrentFlight = true;
    setLoadingOptions(true);
    request<RecoveryOption[]>(`${API_URL}/${flight.id}/recovery-options`)
      .then((recoveryOptions) => {
        if (isCurrentFlight) {
          setOptions(recoveryOptions);
          setError("");
        }
      })
      .catch((requestError: unknown) => {
        if (isCurrentFlight) {
          setError(getErrorMessage(requestError));
        }
      })
      .finally(() => {
        if (isCurrentFlight) {
          setLoadingOptions(false);
        }
      });

    return () => {
      isCurrentFlight = false;
    };
  }, [flight?.id, flight?.status]);

  async function assignAircraft() {
    if (!flight || !selectedAircraftId) {
      return;
    }

    setAssigning(true);
    setError("");
    setSuccess("");
    try {
      const recoveredFlight = await request<Flight>(`${API_URL}/${flight.id}/reassign`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ aircraftId: selectedAircraftId }),
      });
      setFlights([recoveredFlight]);
      setSuccess(`${recoveredFlight.flightNumber} recovered with ${selectedAircraftId}.`);
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setAssigning(false);
    }
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <a className="brand" href="/" aria-label="Flight Operations home">
          <span className="brand-mark" aria-hidden="true">FO</span>
          <span>FLIGHT OPS <span className="brand-divider">/</span> RECOVERY</span>
        </a>
        <span className="demo-label"><span className="live-dot" /> INTERVIEW DEMO</span>
      </header>

      <section className="page-heading">
        <div>
          <p className="eyebrow">OPERATIONS CONTROL</p>
          <h1>Disruption recovery</h1>
          <p className="subtitle">Review the disruption and select a compatible replacement aircraft.</p>
        </div>
        <div className={`system-status ${apiConnected ? "" : loadingFlights ? "connecting" : "disconnected"}`}>
          <span className="status-dot" />
          {apiConnected ? "API CONNECTED" : loadingFlights ? "CONNECTING TO API" : "API OFFLINE"}
        </div>
      </section>

      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {success && <div className="alert alert-success" role="status">{success}</div>}

      {loadingFlights ? (
        <section className="panel loading-panel">Loading flight details…</section>
      ) : flight ? (
        <>
          <section className="flight-card panel">
            <div className="flight-card-heading">
              <div>
                <p className="eyebrow">FLIGHT DISRUPTION</p>
                <h2>{flight.flightNumber}</h2>
              </div>
              <span className={`flight-status ${flight.status.toLowerCase()}`}>
                <span className="status-dot" /> {flight.status}
              </span>
            </div>
            <div className="route">
              <div>
                <span className="field-label">FROM</span>
                <strong>{flight.departureAirport}</strong>
              </div>
              <span className="route-line" aria-hidden="true"><span>✈</span></span>
              <div>
                <span className="field-label">TO</span>
                <strong>{flight.arrivalAirport}</strong>
              </div>
            </div>
            <div className="flight-details">
              <div><span className="field-label">AIRCRAFT TYPE</span><strong>{flight.aircraftType}</strong></div>
              <div><span className="field-label">ASSIGNED AIRCRAFT</span><strong>{flight.assignedAircraftId}</strong></div>
              <div>
                <span className="field-label">{flight.status === "DISRUPTED" ? "DISRUPTION" : "FLIGHT STATUS"}</span>
                <strong>{flight.disruptionReason ?? "Recovery confirmed"}</strong>
              </div>
            </div>
          </section>

          {flight.status === "DISRUPTED" ? (
            <section className="recovery-section">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">RECOVERY PLAN</p>
                  <h2>Available replacement aircraft</h2>
                </div>
                {!loadingOptions && <span className="option-count">{options.length} OPTIONS</span>}
              </div>

              {loadingOptions ? (
                <div className="panel loading-panel">Finding compatible aircraft…</div>
              ) : options.length > 0 ? (
                <>
                  <div className="option-list">
                    {options.map((option) => (
                      <label
                        className={`option-card panel ${selectedAircraftId === option.aircraftId ? "selected" : ""}`}
                        key={option.aircraftId}
                      >
                        <input
                          type="radio"
                          name="replacement-aircraft"
                          value={option.aircraftId}
                          checked={selectedAircraftId === option.aircraftId}
                          onChange={() => setSelectedAircraftId(option.aircraftId)}
                        />
                        <span className="rank-badge">{String(option.rank).padStart(2, "0")}</span>
                        <span className="aircraft-info">
                          <strong>{option.aircraftId}</strong>
                          <span>{option.aircraftType}</span>
                        </span>
                        <span className="airport-info">
                          <span className="field-label">CURRENT LOCATION</span>
                          <strong>{option.currentAirport}</strong>
                        </span>
                        <span className={`position-badge ${option.requiresRepositioning ? "reposition" : "onsite"}`}>
                          <span className="status-dot" />
                          {option.requiresRepositioning ? "Reposition required" : "At departure"}
                        </span>
                      </label>
                    ))}
                  </div>
                  <div className="action-row">
                    <span className="selection-hint">
                      {selectedAircraftId ? `${selectedAircraftId} selected` : "Select an aircraft to continue"}
                    </span>
                    <button
                      className="assign-button"
                      onClick={assignAircraft}
                      disabled={!selectedAircraftId || assigning}
                    >
                      {assigning ? "Assigning…" : "Assign aircraft"}
                      {!assigning && <span aria-hidden="true">→</span>}
                    </button>
                  </div>
                </>
              ) : (
                <div className="panel empty-state">
                  <strong>No compatible aircraft available</strong>
                  <span>There are currently no available aircraft of the required type.</span>
                </div>
              )}
            </section>
          ) : (
            <section className="panel recovered-panel">
              <span className="recovered-icon" aria-hidden="true">✓</span>
              <div><strong>Recovery complete</strong><span>The replacement aircraft has been assigned to this flight.</span></div>
            </section>
          )}
        </>
      ) : (
        <section className="panel empty-state">
          <strong>No flights found</strong>
          <span>The API did not return any flights to display.</span>
        </section>
      )}

      <footer className="footer-note">
        Portfolio demo only · Sample data · Not for real-world dispatch decisions
      </footer>
    </main>
  );
}

export default App;
