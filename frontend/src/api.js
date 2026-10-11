const API_BASE_URL = "/api/flights";

export async function request(url, options) {
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
    const apiError = data;
    throw new Error(apiError?.message ?? `Request failed (${response.status}).`);
  }

  return data;
}

export async function getFlights() {
  return request(API_BASE_URL);
}

export async function getRecoveryHistory() {
  return request(`${API_BASE_URL}/history`);
}

export async function getRecoveryOptions(flightId) {
  return request(`${API_BASE_URL}/${flightId}/recovery-options`);
}

export async function reassignFlight(flightId, aircraftId) {
  return request(`${API_BASE_URL}/${flightId}/reassign`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ aircraftId }),
  });
}
