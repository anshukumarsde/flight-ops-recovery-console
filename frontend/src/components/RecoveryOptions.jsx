function RecoveryOptions({
  flight,
  options,
  selectedAircraftId,
  assigning,
  onSelectAircraft,
  onAssignAircraft,
}) {
  return (
    <section className="card panel recovery-panel">
      <div className="panel-header panel-header-compact">
        <div>
          <p className="eyebrow">Replacement aircraft</p>
          <h2>Recovery options</h2>
        </div>
        <span className="option-count">{options.length} available</span>
      </div>

      {flight.status !== "DISRUPTED" ? (
        <div className="recovery-status-box">
          <div className="success-badge">Recovered</div>
          <p>Flight is operating normally and does not require a disruption recovery assignment.</p>
        </div>
      ) : options.length === 0 ? (
        <p className="empty-state">No compatible aircraft are currently available for this route.</p>
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
                    onChange={() => onSelectAircraft(option.aircraftId)}
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
                        {option.requiresRepositioning ? "Reposition required" : "At departure airport"}
                      </span>
                    </span>
                  </span>
                </label>
              );
            })}
          </div>

          <button
            className="assign-button"
            onClick={onAssignAircraft}
            disabled={!selectedAircraftId || assigning}
          >
            {assigning ? "Assigning recovery…" : "Assign selected aircraft"}
          </button>
        </>
      )}
    </section>
  );
}

export default RecoveryOptions;
