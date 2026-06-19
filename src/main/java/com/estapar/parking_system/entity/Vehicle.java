package com.estapar.parking_system.entity;

import com.estapar.parking_system.entity.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;

    private OffsetDateTime entryTime;

    private OffsetDateTime exitTime;

    private String sector;

    private Double pricePerHour;

    private Double finalPrice;

    @Enumerated(EnumType.STRING)
    private EventType status;
}
