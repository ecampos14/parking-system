package com.estapar.parking_system.dto;

public record SpotDTO(Long id,
                      String sector,
                      Double lat,
                      Double lng,
                      Boolean occupied) {

}
