package com.estapar.parking_system.dto;

import java.util.List;

public record GarageResponseDTO(List<GarageDTO> garage,
                                List<SpotDTO> spots) {
}