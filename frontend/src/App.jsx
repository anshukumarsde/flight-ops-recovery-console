import { useEffect, useState } from "react";
import AppHeader from "./components/AppHeader";
import DispatchBoard from "./components/DispatchBoard";
import FlightManifest from "./components/FlightManifest";
import FlightWorkspace from "./components/FlightWorkspace";
import OperationsSummary from "./components/OperationsSummary";
import RecoveryHistory from "./components/RecoveryHistory";

const API_BASE_URL = "/api/flights";

function errorMessage(error) {
  return error instanceof Error ? error.message : "Something went wrong.";
}

async function apiRequest(url, options) {
  let response;

  try {
    response = await fetch(url, options);
  } catch {
    throw new Error("Cannot reach the API. Start the backend with `mvn spring-boot:run`.");
  }

  const body = await response.text();
  let data;

  try {
    data = JSON.parse(body);
  } catch {
    throw new Error("The API returned an invalid response. Check that the backend is running.");
  }

  if (!response.ok) {
    throw new Error(data?.message ?? `Request failed (${response.status}).`);
  }

  return data;
}

function getFlights() {
  return apiRequest(API_BASE_URL);
}

function getRecoveryHistory() {
  return apiRequest(`${API_BASE_URL}/history`);
}

function getRecoveryOptions(flightId) {
  return apiRequest(`${API_BASE_URL}/${flightId}/recovery-options`);
}

function reassignFlight(flightId, aircraftId) {
  return apiRequest(`${API_BASE_URL}/${flightId}/reassign`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ aircraftId }),
  });
}

function App() {
  const [flights, setFlights] = useState([]);
  const [selectedFlightId, setSelectedFlightId] = useState("");
  const [options, setOptions] = useState([]);
  const [recoveryHistory, setRecoveryHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [selectedAircraftId, setSelectedAircraftId] = useState("");
  const [loading, setLoading] = useState(true);
  const [assigning, setAssigning] = useState(false);
  const [message, setMessage] = useState("");
  const [activeTab, setActiveTab] = useState("Overview");

  const selectedFlight = flights.find((flight) => flight.id === selectedFlightId) ?? flights[0] ?? null;

  async function loadFlights() {
    setLoading(true);
    setMessage("");

    try {
      const nextFlights = await getFlights();
      setFlights(nextFlights);

      if (!nextFlights.length) {
        setSelectedFlightId("");
        setOptions([]);
        setSelectedAircraftId("");
        return;
      }

      const nextSelectedFlight =
        nextFlights.find((flight) => flight.id === selectedFlightId) ?? nextFlights[0];

      setSelectedFlightId(nextSelectedFlight.id);
      setOptions([]);
      setSelectedAircraftId("");
    } catch (error) {
      setFlights([]);
      setMessage(errorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadFlights();
  }, []);

  useEffect(() => {
    if (activeTab !== "History") {
      return;
    }

    let isMounted = true;
    setHistoryLoading(true);

    getRecoveryHistory()
      .then((history) => {
        if (isMounted) {
          setRecoveryHistory(history);
        }
      })
      .catch((error) => {
        if (isMounted) {
          setMessage(errorMessage(error));
        }
      })
      .finally(() => {
        if (isMounted) {
          setHistoryLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [activeTab]);

  useEffect(() => {
    if (!selectedFlight || selectedFlight.status !== "DISRUPTED") {
      setOptions([]);
      setSelectedAircraftId("");
      return;
    }

    let isMounted = true;

    async function refreshOptions() {
      try {
        const recoveryOptions = await getRecoveryOptions(selectedFlight.id);
        if (isMounted) {
          setOptions(recoveryOptions);
        }
      } catch (error) {
        if (isMounted) {
          setOptions([]);
          setMessage(errorMessage(error));
        }
      }
    }

    void refreshOptions();
    setSelectedAircraftId("");

    return () => {
      isMounted = false;
    };
  }, [selectedFlight]);

  async function assignAircraft() {
    if (!selectedFlight || !selectedAircraftId) {
      return;
    }

    setAssigning(true);
    setMessage("");

    try {
      const recoveredFlight = await reassignFlight(selectedFlight.id, selectedAircraftId);
      await loadFlights();
      setMessage(`Recovery completed: ${recoveredFlight.flightNumber} assigned to ${recoveredFlight.assignedAircraftId}.`);
    } catch (error) {
      setMessage(errorMessage(error));
    } finally {
      setAssigning(false);
    }
  }

  function openFlight(flight, tab) {
    setSelectedFlightId(flight.id);
    setActiveTab(tab);
  }

  return (
    <main className="app-shell">
      <div className="app-frame">
        <AppHeader activeTab={activeTab} onTabChange={setActiveTab} />
        <OperationsSummary flights={flights} />

        {loading && <p className="loading-state">Loading operations view…</p>}

        {!loading && message && (
          <div className="message" role="status">
            <p>{message}</p>
            {!selectedFlight && <button onClick={() => void loadFlights()}>Retry</button>}
          </div>
        )}

        {!loading && activeTab === "Flights" && (
          <FlightManifest flights={flights} onOpenFlight={openFlight} />
        )}

        {!loading && activeTab === "Dispatch" && (
          <DispatchBoard flights={flights} onOpenFlight={openFlight} />
        )}

        {!loading && activeTab === "History" && (
          <RecoveryHistory history={recoveryHistory} loading={historyLoading} />
        )}

        {!loading && selectedFlight && (activeTab === "Overview" || activeTab === "Recovery") && (
          <FlightWorkspace
            flights={flights}
            selectedFlight={selectedFlight}
            selectedFlightId={selectedFlightId}
            options={options}
            selectedAircraftId={selectedAircraftId}
            assigning={assigning}
            onSelectFlight={setSelectedFlightId}
            onSelectAircraft={setSelectedAircraftId}
            onAssignAircraft={assignAircraft}
          />
        )}

        {!loading && activeTab === "Recovery" && !selectedFlight && (
          <section className="card panel page-panel">
            <p className="eyebrow">Recovery management</p>
            <h2>No flight selected</h2>
            <p className="empty-state">There are no flights available to review for recovery.</p>
          </section>
        )}

        <div className="footer-note">Dispatch support • Fleet operations • Recovery console</div>
      </div>
    </main>
  );
}

export default App;
