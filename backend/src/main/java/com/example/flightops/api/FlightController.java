package com.example.flightops.api;

import com.example.flightops.model.Flight;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @GetMapping
    public List<Flight> getFlights() {
        return List.of(new Flight(
                "FL-1001",
                "NJ 1001",
                "KTEB",
                "KMIA",
                "Citation Latitude",
                "N700NJ",
                "DISRUPTED",
                "Maintenance hold"
        ));
    }
}
