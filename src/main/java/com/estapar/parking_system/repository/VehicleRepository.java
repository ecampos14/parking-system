package com.estapar.parking_system.repository;

import com.estapar.parking_system.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    @Query("""
            SELECT COALESCE(SUM(v.finalPrice), 0)
            FROM Vehicle v
            WHERE v.sector = :sector
            AND DATE(v.exitTime) = :date
            AND v.status = com.estapar.parking_system.entity.enums.EventType.EXIT
            """)
    Double sumRevenueBySectorAndDate(@Param("sector") String sector, @Param("date") LocalDate date);
}