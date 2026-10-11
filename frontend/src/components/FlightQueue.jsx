function FlightQueue({ flights, selectedFlightId, onSelectFlight }) {
  return (
    <aside className="card panel flight-list-panel">
      <div className="panel-header panel-header-compact">
        <div>
          <p className="eyebrow">Active flights</p>
          <h2>Flight queue</h2>
        </div>
      </div>

      <div className="flight-list">
        {flights.map((flight) => (
          <button
            type="button"
            key={flight.id}
            className={`flight-list-item ${selectedFlightId === flight.id ? "active" : ""}`}
            onClick={() => onSelectFlight(flight.id)}
          >
            <div className="flight-item-header">
              <strong>{flight.flightNumber}</strong>
              <span className={`status-badge ${flight.status.toLowerCase()}`}>{flight.status}</span>
            </div>
            <div className="flight-item-route">
              <span>{flight.departureAirport}</span>
              <span className="route-mini">→</span>
              <span>{flight.arrivalAirport}</span>
            </div>
            <small>{flight.disruptionReason ?? "Operationally stable"}</small>
          </button>
        ))}
      </div>
    </aside>
  );
}

export default FlightQueue;
