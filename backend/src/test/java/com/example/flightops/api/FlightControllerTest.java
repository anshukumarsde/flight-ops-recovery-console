package com.example.flightops.api;

import com.example.flightops.repository.FlightRepository;
import com.example.flightops.service.FlightRecoveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
@Import({FlightRecoveryService.class, FlightRepository.class})
class FlightControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsTheDemoDisruptedFlight() throws Exception {
        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("FL-1001"))
                .andExpect(jsonPath("$[0].status").value("DISRUPTED"));
    }

    @Test
    void listsFlightsWhenTheUrlHasATrailingSlash() throws Exception {
        mockMvc.perform(get("/api/flights/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("FL-1001"));
    }

    @Test
    void listsAvailableCompatibleAircraftWithDepartureAirportFirst() throws Exception {
        mockMvc.perform(get("/api/flights/FL-1001/recovery-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].aircraftId").value("N701NJ"))
                .andExpect(jsonPath("$[0].requiresRepositioning").value(false))
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[1].aircraftId").value("N702NJ"))
                .andExpect(jsonPath("$[1].requiresRepositioning").value(true))
                .andExpect(jsonPath("$[1].rank").value(2));
    }

    @Test
    void returnsNotFoundForAnUnknownFlight() throws Exception {
        mockMvc.perform(get("/api/flights/UNKNOWN/recovery-options"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void reassignsFlightToAnAvailableCompatibleAircraft() throws Exception {
        mockMvc.perform(post("/api/flights/FL-1001/reassign")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aircraftId\":\"N701NJ\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAircraftId").value("N701NJ"))
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        mockMvc.perform(get("/api/flights"))
                .andExpect(jsonPath("$[0].status").value("RECOVERED"));
    }

    @Test
    void rejectsUnavailableIncompatibleAndUnknownAircraft() throws Exception {
        mockMvc.perform(post("/api/flights/FL-1001/reassign")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aircraftId\":\"N704NJ\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/flights/FL-1001/reassign")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aircraftId\":\"N703NJ\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/flights/FL-1001/reassign")
                        .contentType(APPLICATION_JSON)
                        .content("{\"aircraftId\":\"UNKNOWN\"}"))
                .andExpect(status().isNotFound());
    }
}
