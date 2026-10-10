package com.example.flightops.api;

import com.example.flightops.model.Flight;
import com.example.flightops.model.RecoveryOption;
import com.example.flightops.service.FlightRecoveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightRecoveryService flightRecoveryService;

    public FlightController(FlightRecoveryService flightRecoveryService) {
        this.flightRecoveryService = flightRecoveryService;
    }

    @GetMapping({"", "/"})
    public List<Flight> getFlights() {
        return flightRecoveryService.getFlights();
    }

    @GetMapping("/{id}/recovery-options")
    public List<RecoveryOption> getRecoveryOptions(@PathVariable String id) {
        return flightRecoveryService.getRecoveryOptions(id);
    }

    @PostMapping("/{id}/reassign")
    public Flight reassignFlight(@PathVariable String id, @RequestBody ReassignRequest request) {
        return flightRecoveryService.reassignFlight(id, request.aircraftId());
    }
}
