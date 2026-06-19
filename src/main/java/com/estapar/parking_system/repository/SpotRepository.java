package com.estapar.parking_system.repository;

import com.estapar.parking_system.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    Optional<Spot>findFirstByOccupiedFalse();

    Optional<Spot> findByLatAndLng(Double lat, Double lng);

    Optional<Spot> findFirstBySectorAndOccupiedTrue(String sector);

    long countBySector(String sector);

    long countBySectorAndOccupiedTrue(String sector);
}
