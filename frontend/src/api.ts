export type Flight = {
  id: string;
  flightNumber: string;
  departureAirport: string;
  arrivalAirport: string;
  aircraftType: string;
  assignedAircraftId: string;
  status: "DISRUPTED" | "RECOVERED";
  disruptionReason: string | null;
};

export type RecoveryOption = {
  aircraftId: string;
  aircraftType: string;
  currentAirport: string;
  requiresRepositioning: boolean;
  rank: number;
};

type ApiError = {
  message?: string;
};

const API_BASE_URL = "/api/flights";

export async function request<T>(url: string, options?: RequestInit): Promise<T> {
  let response: Response;

  try {
    response = await fetch(url, options);
  } catch {
    throw new Error("Cannot reach the API. Start the backend with `mvn spring-boot:run`.");
  }

  const body = await response.text();
  let data: unknown;

  try {
    data = JSON.parse(body);
  } catch {
    throw new Error("The API returned an invalid response. Check that the backend is running.");
  }

  if (!response.ok) {
    const apiError = data as ApiError;
    throw new Error(apiError.message ?? `Request failed (${response.status}).`);
  }

  return data as T;
}

export async function getFlights(): Promise<Flight[]> {
  return request<Flight[]>(API_BASE_URL);
}

export async function getRecoveryOptions(flightId: string): Promise<RecoveryOption[]> {
  return request<RecoveryOption[]>(`${API_BASE_URL}/${flightId}/recovery-options`);
}

export async function reassignFlight(flightId: string, aircraftId: string): Promise<Flight> {
  return request<Flight>(`${API_BASE_URL}/${flightId}/reassign`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ aircraftId }),
  });
}
