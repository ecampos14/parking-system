package com.estapar.parking_system.service.impl;

import com.estapar.parking_system.dto.RevenueResponseDTO;
import com.estapar.parking_system.repository.VehicleRepository;
import com.estapar.parking_system.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final VehicleRepository vehicleRepository;

    @Override
    public RevenueResponseDTO getRevenue(LocalDate date, String sector) {
        Double amount = vehicleRepository.sumRevenueBySectorAndDate(sector, date);
        if (amount == null) {
            amount = 0.0;
        }
        return new RevenueResponseDTO(amount, "BRL", OffsetDateTime.now());
    }
}
