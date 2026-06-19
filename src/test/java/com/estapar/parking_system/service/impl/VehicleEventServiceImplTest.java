package com.estapar.parking_system.service.impl;

import com.estapar.parking_system.dto.VehicleEventDTO;
import com.estapar.parking_system.entity.GarageSector;
import com.estapar.parking_system.entity.Spot;
import com.estapar.parking_system.entity.Vehicle;
import com.estapar.parking_system.entity.enums.EventType;
import com.estapar.parking_system.repository.GarageSectorRepository;
import com.estapar.parking_system.repository.SpotRepository;
import com.estapar.parking_system.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleEventServiceImplTest {

    private static final OffsetDateTime ENTRY_TIME = OffsetDateTime.parse("2026-06-19T14:00:00Z");
    private static final OffsetDateTime EXIT_TIME = OffsetDateTime.parse("2026-06-19T15:10:00Z");
    private static final OffsetDateTime FREE_EXIT_TIME = OffsetDateTime.parse("2026-06-19T14:20:00Z");

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private GarageSectorRepository garageSectorRepository;

    @InjectMocks
    private VehicleEventServiceImpl service;

    @BeforeEach
    void setup() {
        service.init();
    }

    @Test
    void shouldSaveVehicleAndOccupySpotOnEntry() {
        VehicleEventDTO event = new VehicleEventDTO("EST2026", ENTRY_TIME, null, EventType.ENTRY, null, null);
        Spot spot = Spot.builder().id(1L).sector("A").occupied(false).build();

        GarageSector sector = GarageSector.builder().sector("A").basePrice(40.5).build();

        when(spotRepository.findFirstByOccupiedFalse()).thenReturn(Optional.of(spot));
        when(garageSectorRepository.findBySector("A")).thenReturn(Optional.of(sector));
        when(spotRepository.countBySector("A")).thenReturn(10L);
        when(spotRepository.countBySectorAndOccupiedTrue("A")).thenReturn(0L);

        service.process(event);
        assertTrue(spot.getOccupied());
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());
        Vehicle saved = captor.getValue();

        assertEquals("EST2026", saved.getLicensePlate());
        assertEquals("A", saved.getSector());
        assertEquals(EventType.ENTRY, saved.getStatus());
        assertEquals(36.45, saved.getPricePerHour());

        verify(spotRepository).save(spot);
    }

    @Test
    void shouldCalculateFinalPriceAndReleaseSpotOnExit() {
        VehicleEventDTO event = new VehicleEventDTO("PARK001", null, EXIT_TIME, EventType.EXIT, null, null);
        Vehicle vehicle = Vehicle.builder().id(1L).licensePlate("PARK001").entryTime(ENTRY_TIME).sector("A").pricePerHour(36.45).status(EventType.ENTRY).build();

        Spot spot = Spot.builder().id(1L).sector("A").occupied(true).build();

        when(vehicleRepository.findByLicensePlate("PARK001")).thenReturn(Optional.of(vehicle));
        when(spotRepository.findFirstBySectorAndOccupiedTrue("A")).thenReturn(Optional.of(spot));
        service.process(event);

        assertEquals(EventType.EXIT, vehicle.getStatus());
        assertEquals(72.9, vehicle.getFinalPrice());
        assertFalse(spot.getOccupied());

        verify(vehicleRepository).save(vehicle);
        verify(spotRepository).save(spot);
    }

    @Test
    void shouldNotChargeWhenVehicleLeavesBeforeThirtyMinutes() {
        VehicleEventDTO event = new VehicleEventDTO("FAST001", null, FREE_EXIT_TIME, EventType.EXIT, null, null);
        Vehicle vehicle = Vehicle.builder().licensePlate("FAST001").entryTime(ENTRY_TIME).sector("A").pricePerHour(36.45).status(EventType.ENTRY).build();

        Spot spot = Spot.builder().sector("A").occupied(true).build();

        when(vehicleRepository.findByLicensePlate("FAST001")).thenReturn(Optional.of(vehicle));
        when(spotRepository.findFirstBySectorAndOccupiedTrue("A")).thenReturn(Optional.of(spot));
        service.process(event);

        assertEquals(0.0, vehicle.getFinalPrice());
        assertEquals(EventType.EXIT, vehicle.getStatus());
        assertFalse(spot.getOccupied());
    }

    @Test
    void shouldThrowExceptionWhenGarageIsFull() {
        VehicleEventDTO event = new VehicleEventDTO("FULL999", ENTRY_TIME, null, EventType.ENTRY, null, null);

        when(spotRepository.findFirstByOccupiedFalse()).thenReturn(Optional.empty());
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.process(event));
        assertEquals("Garagem lotada", exception.getMessage());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsNotFoundOnExit() {
        VehicleEventDTO event = new VehicleEventDTO("NOTFOUND", null, EXIT_TIME, EventType.EXIT, null, null);

        when(vehicleRepository.findByLicensePlate("NOTFOUND")).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.process(event));
        assertEquals("Veículo não encontrado", exception.getMessage());
    }
}