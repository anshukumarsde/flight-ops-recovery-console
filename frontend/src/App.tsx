import { useEffect, useState } from "react";
import { getFlights, getRecoveryOptions, reassignFlight, type Flight, type RecoveryOption } from "./api";

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
      const flights = await getFlights();
      const demoFlight = flights[0];
      setFlight(demoFlight ?? null);
      setOptions([]);
      setSelectedAircraftId("");

      if (demoFlight?.status === "DISRUPTED") {
        const recoveryOptions = await getRecoveryOptions(demoFlight.id);
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
      const recoveredFlight = await reassignFlight(flight.id, selectedAircraftId);
      setFlight(recoveredFlight);
      setMessage(`Flight recovered with aircraft ${recoveredFlight.assignedAircraftId}.`);
    } catch (error) {
      setMessage(errorMessage(error));
    } finally {
      setAssigning(false);
    }
  }

  return (
    <main className="app-shell">
      <div className="app-frame">
        <header className="topbar">
          <div className="brand-group">
            <div className="brand-mark">FO</div>
            <div>
              <p className="brand-kicker">FlightOps</p>
              <p className="brand-title">Recovery Console</p>
            </div>
          </div>
          <nav className="topnav" aria-label="Main navigation">
            <span className="nav-link active">Operations</span>
            <span className="nav-link">Flights</span>
            <span className="nav-link">Dispatch</span>
          </nav>
        </header>

        {loading && <p className="loading-state">Loading flight…</p>}

        {!loading && message && (
          <div className="message" role="status">
            <p>{message}</p>
            {!flight && <button onClick={() => void loadFlight()}>Retry</button>}
          </div>
        )}

        {!loading && flight && (
          <div className="content-layout">
            <section className="card flight-overview">
              <div className="flight-header-row">
                <div>
                  <p className="eyebrow">Flight status</p>
                  <div className="flight-number-row">
                    <h1>{flight.flightNumber}</h1>
                    <span className={`status-badge ${flight.status.toLowerCase()}`}>{flight.status}</span>
                  </div>
                </div>
                <div className="status-pill">Recovery action</div>
              </div>

              <div className="route-panel">
                <div className="route-block">
                  <span className="route-label">Departure</span>
                  <strong>{flight.departureAirport}</strong>
                </div>
                <div className="route-arrow" aria-hidden="true">→</div>
                <div className="route-block">
                  <span className="route-label">Arrival</span>
                  <strong>{flight.arrivalAirport}</strong>
                </div>
              </div>

              <dl className="flight-details">
                <div className="detail-block">
                  <dt>Aircraft type</dt>
                  <dd>{flight.aircraftType}</dd>
                </div>
                <div className="detail-block">
                  <dt>Assigned aircraft</dt>
                  <dd>{flight.assignedAircraftId}</dd>
                </div>
                {flight.disruptionReason && (
                  <div className="detail-block full-width">
                    <dt>Reason</dt>
                    <dd>{flight.disruptionReason}</dd>
                  </div>
                )}
              </dl>
            </section>

            {flight.status === "DISRUPTED" ? (
              <section className="card recovery-panel">
                <div className="panel-header">
                  <div>
                    <p className="eyebrow">Replacement aircraft</p>
                    <h2>Recovery options</h2>
                  </div>
                  <span className="option-count">{options.length} available</span>
                </div>

                {options.length === 0 ? (
                  <p className="empty-state">No compatible aircraft are available.</p>
                ) : (
                  <>
                    <div className="options">
                      {options.map((option) => {
                        const isSelected = selectedAircraftId === option.aircraftId;

                        return (
                          <label className={`option ${isSelected ? "selected" : ""}`} key={option.aircraftId}>
                            <input
                              type="radio"
                              name="aircraft"
                              value={option.aircraftId}
                              checked={isSelected}
                              onChange={() => setSelectedAircraftId(option.aircraftId)}
                            />
                            <span className="option-rank">{option.rank}</span>
                            <span className="option-main">
                              <span className="aircraft-title-row">
                                <strong>{option.aircraftId}</strong>
                                <span className="aircraft-type">{option.aircraftType}</span>
                              </span>
                              <span className="airport-row">
                                <span className="airport-name">{option.currentAirport}</span>
                                <span className={`airport-state ${option.requiresRepositioning ? "reposition" : "on-site"}`}>
                                  {option.requiresRepositioning
                                    ? "Reposition required"
                                    : "Already at departure airport"}
                                </span>
                              </span>
                            </span>
                          </label>
                        );
                      })}
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
              <section className="card success-panel">
                <div className="success-badge">Recovered</div>
                <h2>Recovery complete</h2>
                <p>Flight {flight.flightNumber} is ready for follow-up and the selected replacement aircraft is assigned.</p>
              </section>
            )}
          </div>
        )}

      </div>
    </main>
  );
}

export default App;
