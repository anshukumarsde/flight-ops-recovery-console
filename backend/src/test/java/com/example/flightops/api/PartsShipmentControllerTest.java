package com.example.flightops.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:parts-shipment-tests;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class PartsShipmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void tracksShipmentProgressAndPersistsItsHistory() throws Exception {
        mockMvc.perform(get("/api/parts/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].trackingNumber").value("DHL-74291058"))
                .andExpect(jsonPath("$[1].currentLocation").value("Frankfurt, Germany"))
                .andExpect(jsonPath("$[1].updates[0].note").value("Departed origin facility; customs cleared"));

        mockMvc.perform(post("/api/parts/shipments/PS-1001/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "OUT_FOR_DELIVERY",
                                  "location": "Miami, FL",
                                  "estimatedDelivery": "2026-10-15",
                                  "note": "At destination facility"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OUT_FOR_DELIVERY"))
                .andExpect(jsonPath("$.currentLocation").value("Miami, FL"))
                .andExpect(jsonPath("$.updates[0].note").value("At destination facility"));

        mockMvc.perform(get("/api/parts/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PS-1001"))
                .andExpect(jsonPath("$[0].currentLocation").value("Miami, FL"))
                .andExpect(jsonPath("$[0].updates[0].note").value("At destination facility"));
    }
}
