package com.estapar.parking_system.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "garage_sector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GarageSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sector;

    private Double basePrice;

    private Integer maxCapacity;

    private Integer occupied = 0;
}
