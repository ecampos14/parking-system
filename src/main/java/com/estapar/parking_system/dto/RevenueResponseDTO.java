package com.estapar.parking_system.dto;

import java.time.OffsetDateTime;

public record RevenueResponseDTO(Double amount,
                                 String currency,
                                 OffsetDateTime timestamp) {
}
