package com.estapar.parking_system.service;

import com.estapar.parking_system.dto.RevenueResponseDTO;

import java.time.LocalDate;

public interface RevenueService {

    RevenueResponseDTO getRevenue(LocalDate date, String sector);
}