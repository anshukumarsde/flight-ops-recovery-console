function DispatchBoard({ flights, onOpenFlight }) {
  const hasDisruptions = flights.some((flight) => flight.status === "DISRUPTED");

  return (
    <section className="card panel page-panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Live coordination</p>
          <h2>Dispatch board</h2>
        </div>
        <span className={`status-pill ${hasDisruptions ? "attention" : ""}`}>
          {hasDisruptions ? "Action required" : "Operations stable"}
        </span>
      </div>
      <div className="dispatch-board">
        {flights.length === 0 ? (
          <p className="empty-state">No flight activity to coordinate.</p>
        ) : (
          flights.map((flight) => {
            const disrupted = flight.status === "DISRUPTED";
            return (
              <article className={`dispatch-row ${disrupted ? "priority" : ""}`} key={flight.id}>
                <span className={`dispatch-indicator ${disrupted ? "warning" : "clear"}`} aria-hidden="true" />
                <div className="dispatch-flight">
                  <strong>{flight.flightNumber}</strong>
                  <span>{flight.departureAirport} → {flight.arrivalAirport}</span>
                </div>
                <div className="dispatch-update">
                  <span>{disrupted ? "Disruption" : "Aircraft assignment"}</span>
                  <strong>{flight.disruptionReason ?? flight.assignedAircraftId}</strong>
                </div>
                <span className={`status-badge ${flight.status.toLowerCase()}`}>{flight.status}</span>
                <button
                  type="button"
                  className="text-button"
                  onClick={() => onOpenFlight(flight, disrupted ? "Recovery" : "Flights")}
                >
                  {disrupted ? "Coordinate recovery" : "View flight"}
                </button>
              </article>
            );
          })
        )}
      </div>
    </section>
  );
}

export default DispatchBoard;
