package com.estapar.parking_system.dto;

import com.estapar.parking_system.entity.enums.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record VehicleEventDTO(@JsonProperty("license_plate")
                              String licensePlate,

                              @JsonProperty("entry_time")
                              OffsetDateTime entryTime,

                              @JsonProperty("exit_time")
                              OffsetDateTime exitTime,

                              @JsonProperty("event_type")
                              EventType eventType,

                              Double lat,

                              Double lng) {
}
