function FlightOverview({ flight }) {
  return (
    <section className="card panel flight-overview-panel">
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

      <div className="metrics-row">
        <div className="metric-box">
          <span>Aircraft type</span>
          <strong>{flight.aircraftType}</strong>
        </div>
        <div className="metric-box">
          <span>Assigned aircraft</span>
          <strong>{flight.assignedAircraftId}</strong>
        </div>
        <div className="metric-box">
          <span>Ops note</span>
          <strong>{flight.disruptionReason ?? "Normal schedule"}</strong>
        </div>
      </div>

      <div className="event-log">
        <div className="event-row">
          <span className="event-dot" />
          <div>
            <strong>Maintenance review</strong>
            <p>{flight.disruptionReason ?? "No disruption reported."}</p>
          </div>
        </div>
        <div className="event-row">
          <span className="event-dot muted" />
          <div>
            <strong>Recovery window</strong>
            <p>
              {flight.status === "DISRUPTED"
                ? "Priority replacement required within 45 minutes."
                : "Flight remains on schedule."}
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}

export default FlightOverview;
