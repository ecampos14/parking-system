package com.estapar.parking_system.service;


import com.estapar.parking_system.dto.VehicleEventDTO;

public interface VehicleEventService {

    void process(VehicleEventDTO event);

}
