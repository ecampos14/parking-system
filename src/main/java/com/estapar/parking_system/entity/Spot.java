package com.estapar.parking_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "spot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spot {

    @Id
    private Long id;

    private String sector;

    private Double lat;

    private Double lng;

    private Boolean occupied = false;
}
