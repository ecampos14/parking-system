package com.estapar.parking_system.controller;

import com.estapar.parking_system.dto.VehicleEventDTO;
import com.estapar.parking_system.service.VehicleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final VehicleEventService vehicleEventService;

    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestBody VehicleEventDTO event) {
        vehicleEventService.process(event);
        return ResponseEntity.ok().build();
    }
}