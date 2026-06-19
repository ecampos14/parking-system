package com.estapar.parking_system.service.impl;

import com.estapar.parking_system.dto.RevenueResponseDTO;
import com.estapar.parking_system.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RevenueServiceImpl revenueService;

    @Test
    void shouldReturnRevenueBySectorAndDate() {
        LocalDate date = LocalDate.of(2026, 6, 19);
        String sector = "A";

        when(vehicleRepository.sumRevenueBySectorAndDate(sector, date)).thenReturn(72.9);
        RevenueResponseDTO response = revenueService.getRevenue(date, sector);

        assertEquals(72.9, response.amount());
        assertEquals("BRL", response.currency());
        assertNotNull(response.timestamp());
        verify(vehicleRepository).sumRevenueBySectorAndDate(sector, date);
    }

    @Test
    void shouldReturnZeroWhenThereIsNoRevenue() {
        LocalDate date = LocalDate.of(2026, 6, 19);
        String sector = "B";

        when(vehicleRepository.sumRevenueBySectorAndDate(sector, date)).thenReturn(null);
        RevenueResponseDTO response = revenueService.getRevenue(date, sector);

        assertEquals(0.0, response.amount());
        assertEquals("BRL", response.currency());
        assertNotNull(response.timestamp());
        verify(vehicleRepository).sumRevenueBySectorAndDate(sector, date);
    }
}