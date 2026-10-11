function FlightManifest({ flights, onOpenFlight }) {
  return (
    <section className="card panel page-panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Operations schedule</p>
          <h2>Flight manifest</h2>
        </div>
        <span className="option-count">{flights.length} flights</span>
      </div>
      {flights.length === 0 ? (
        <p className="empty-state">No flights are currently available.</p>
      ) : (
        <div className="flight-manifest">
          {flights.map((flight) => (
            <article className="manifest-row" key={flight.id}>
              <div className="manifest-flight">
                <strong>{flight.flightNumber}</strong>
                <span>{flight.id}</span>
              </div>
              <div className="manifest-route">
                <strong>{flight.departureAirport}</strong>
                <span aria-hidden="true">→</span>
                <strong>{flight.arrivalAirport}</strong>
              </div>
              <div className="manifest-assignment">
                <span>Assigned aircraft</span>
                <strong>{flight.assignedAircraftId}</strong>
              </div>
              <span className={`status-badge ${flight.status.toLowerCase()}`}>{flight.status}</span>
              <button
                type="button"
                className="text-button"
                onClick={() => onOpenFlight(flight, flight.status === "DISRUPTED" ? "Recovery" : "Flights")}
              >
                {flight.status === "DISRUPTED" ? "Manage recovery" : "Select flight"}
              </button>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

export default FlightManifest;
