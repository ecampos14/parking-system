package com.estapar.parking_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GarageDTO(String sector,
                        @JsonProperty("base_price")
                        Double basePrice,
                        @JsonProperty("max_capacity")
                        Integer maxCapacity) {
}