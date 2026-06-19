package com.estapar.parking_system.service.impl;

import com.estapar.parking_system.dto.VehicleEventDTO;
import com.estapar.parking_system.entity.GarageSector;
import com.estapar.parking_system.entity.Spot;
import com.estapar.parking_system.entity.Vehicle;
import com.estapar.parking_system.entity.enums.EventType;
import com.estapar.parking_system.repository.GarageSectorRepository;
import com.estapar.parking_system.repository.SpotRepository;
import com.estapar.parking_system.repository.VehicleRepository;
import com.estapar.parking_system.service.VehicleEventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class VehicleEventServiceImpl implements VehicleEventService {

    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final GarageSectorRepository garageSectorRepository;
    private final Map<EventType, Consumer<VehicleEventDTO>> handlers = new EnumMap<>(EventType.class);

    @PostConstruct
    public void init() {
        handlers.put(EventType.ENTRY, this::handleEntry);
        handlers.put(EventType.PARKED, this::handleParked);
        handlers.put(EventType.EXIT, this::handleExit);
    }

    @Override
    public void process(VehicleEventDTO event) {
        Consumer<VehicleEventDTO> handler = handlers.get(event.eventType());

        if (handler == null) {
            throw new IllegalArgumentException("Evento não suportado");
        }

        handler.accept(event);
    }

    private void handleEntry(VehicleEventDTO event) {
        Spot spot = spotRepository.findFirstByOccupiedFalse().orElseThrow(() -> new IllegalStateException("Garagem lotada"));
        GarageSector sector = garageSectorRepository.findBySector(spot.getSector()).orElseThrow(() -> new IllegalArgumentException("Setor não encontrado"));

        double pricePerHour = calculateDynamicPrice(sector);
        spot.setOccupied(true);
        spotRepository.save(spot);

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(event.licensePlate())
                .entryTime(event.entryTime())
                .sector(spot.getSector())
                .pricePerHour(pricePerHour)
                .status(EventType.ENTRY).build();
        vehicleRepository.save(vehicle);
    }

    private void handleParked(VehicleEventDTO event) {
        Spot spot = spotRepository.findByLatAndLng(event.lat(), event.lng()).orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
        spot.setOccupied(true);
        spotRepository.save(spot);
    }

    private void handleExit(VehicleEventDTO event) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(event.licensePlate()).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        vehicle.setExitTime(event.exitTime());
        long minutes = Duration.between(vehicle.getEntryTime(), vehicle.getExitTime()).toMinutes();
        double price = 0;

        if (minutes > 30) {
            long hours = (long) Math.ceil(minutes / 60.0);
            price = hours * vehicle.getPricePerHour();
        }

        vehicle.setFinalPrice(price);
        vehicle.setStatus(EventType.EXIT);
        vehicleRepository.save(vehicle);

        Spot spot = spotRepository.findFirstBySectorAndOccupiedTrue(vehicle.getSector()).orElseThrow(() -> new IllegalArgumentException("Vaga ocupada não encontrada"));
        spot.setOccupied(false);
        spotRepository.save(spot);
    }

    private double calculateDynamicPrice(GarageSector sector) {
        long totalSpots = spotRepository.countBySector(sector.getSector());
        long occupiedSpots = spotRepository.countBySectorAndOccupiedTrue(sector.getSector());
        double occupancy = (double) occupiedSpots / totalSpots;

        NavigableMap<Double, Double> dynamicRules = new TreeMap<>();
        dynamicRules.put(0.249999, 0.90);
        dynamicRules.put(0.50, 1.00);
        dynamicRules.put(0.75, 1.10);
        dynamicRules.put(1.00, 1.25);
        Double multiplier = dynamicRules.ceilingEntry(occupancy).getValue();

        return sector.getBasePrice() * multiplier;
    }
}