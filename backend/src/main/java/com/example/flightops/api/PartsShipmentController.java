package com.example.flightops.api;

import com.example.flightops.model.PartsShipment;
import com.example.flightops.service.PartsShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/parts/shipments")
public class PartsShipmentController {
    private final PartsShipmentService partsShipmentService;

    public PartsShipmentController(PartsShipmentService partsShipmentService) {
        this.partsShipmentService = partsShipmentService;
    }

    @GetMapping({"", "/"})
    public List<PartsShipment> getShipments() {
        return partsShipmentService.getShipments();
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public PartsShipment createOrder(@RequestBody CreatePartsOrderRequest request) {
        return partsShipmentService.createOrder(request);
    }

    @PatchMapping("/{id}")
    public PartsShipment addUpdate(@PathVariable String id, @RequestBody PartsShipmentUpdateRequest request) {
        return partsShipmentService.addUpdate(id, request);
    }
}
