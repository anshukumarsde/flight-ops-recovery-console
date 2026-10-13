package com.example.flightops.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    void ordersPartsAndUpdatesCurrentTracking() throws Exception {
        mockMvc.perform(get("/api/parts/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].trackingNumber").value("DHL-74291058"))
                .andExpect(jsonPath("$[1].currentLocation").value("Frankfurt, Germany"));

        mockMvc.perform(patch("/api/parts/shipments/PS-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_TRANSIT",
                                  "location": "Miami, FL",
                                  "estimatedDelivery": "2026-10-15"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.currentLocation").value("Miami, FL"));

        mockMvc.perform(get("/api/parts/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PS-1001"))
                .andExpect(jsonPath("$[0].currentLocation").value("Miami, FL"));

        mockMvc.perform(post("/api/parts/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partNumber": "FLT-9001",
                                  "partName": "Fuel filter",
                                  "quantity": 2,
                                  "supplier": "Example Aviation Parts",
                                  "origin": "Dallas, TX",
                                  "destination": "Teterboro, NJ",
                                  "estimatedDelivery": "2030-10-15"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partNumber").value("FLT-9001"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("ORDERED"))
                .andExpect(jsonPath("$.currentLocation").value("Awaiting supplier dispatch"));
    }
}
