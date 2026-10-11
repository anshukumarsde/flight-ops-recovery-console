import FlightOverview from "./FlightOverview";
import FlightQueue from "./FlightQueue";
import RecoveryOptions from "./RecoveryOptions";

function FlightWorkspace({
  flights,
  selectedFlight,
  selectedFlightId,
  options,
  selectedAircraftId,
  assigning,
  onSelectFlight,
  onSelectAircraft,
  onAssignAircraft,
}) {
  return (
    <div className="workspace-grid">
      <FlightQueue
        flights={flights}
        selectedFlightId={selectedFlightId}
        onSelectFlight={onSelectFlight}
      />
      <FlightOverview flight={selectedFlight} />
      <RecoveryOptions
        flight={selectedFlight}
        options={options}
        selectedAircraftId={selectedAircraftId}
        assigning={assigning}
        onSelectAircraft={onSelectAircraft}
        onAssignAircraft={onAssignAircraft}
      />
    </div>
  );
}

export default FlightWorkspace;
