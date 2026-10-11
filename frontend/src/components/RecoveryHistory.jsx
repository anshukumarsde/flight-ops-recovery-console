function RecoveryHistory({ history, loading }) {
  return (
    <section className="card panel page-panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Completed incidents</p>
          <h2>Recovery history</h2>
        </div>
        <span className="option-count">{history.length} recoveries</span>
      </div>

      {loading ? (
        <p className="empty-state">Loading recovery history…</p>
      ) : history.length === 0 ? (
        <p className="empty-state">No incidents have been recovered yet.</p>
      ) : (
        <div className="recovery-history-list">
          {history.map((incident) => (
            <article className="history-card" key={incident.id}>
              <div className="history-heading">
                <div>
                  <p className="eyebrow">{incident.flightId}</p>
                  <h3>{incident.flightNumber}</h3>
                </div>
                <span className="status-badge recovered">Recovered</span>
              </div>

              <div className="history-route">
                <strong>{incident.departureAirport}</strong>
                <span aria-hidden="true">→</span>
                <strong>{incident.arrivalAirport}</strong>
              </div>

              <div className="history-details">
                <div>
                  <span>Incident</span>
                  <strong>{incident.disruptionReason ?? "Reason not recorded"}</strong>
                </div>
                <div>
                  <span>Aircraft replaced</span>
                  <strong>{incident.previousAircraftId}</strong>
                </div>
                <div>
                  <span>Recovery aircraft</span>
                  <strong>{incident.assignedAircraftId}</strong>
                </div>
                <div>
                  <span>Aircraft position</span>
                  <strong>{incident.aircraftCurrentAirport}</strong>
                </div>
              </div>

              <div className="history-footer">
                <span>{incident.repositionRequired ? "Repositioning required" : "Aircraft at departure airport"}</span>
                <time dateTime={incident.recoveredAt}>{new Date(incident.recoveredAt).toLocaleString()}</time>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

export default RecoveryHistory;
