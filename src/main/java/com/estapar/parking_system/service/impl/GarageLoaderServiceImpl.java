package com.estapar.parking_system.service.impl;

import com.estapar.parking_system.dto.GarageResponseDTO;
import com.estapar.parking_system.entity.GarageSector;
import com.estapar.parking_system.entity.Spot;
import com.estapar.parking_system.repository.GarageSectorRepository;
import com.estapar.parking_system.repository.SpotRepository;
import com.estapar.parking_system.service.GarageLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GarageLoaderServiceImpl implements GarageLoaderService {

    private final RestTemplate restTemplate;

    private final GarageSectorRepository garageSectorRepository;

    private final SpotRepository spotRepository;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void loadGarage() {
        try {
            GarageResponseDTO response = restTemplate.getForObject("http://localhost:3000/garage", GarageResponseDTO.class);
            if (response == null) {
                throw new IllegalStateException("Não foi possível carregar a configuração da garagem");
            }

            response.garage().forEach(garage -> {
                GarageSector sector = GarageSector.builder()
                        .sector(garage.sector())
                        .basePrice(garage.basePrice())
                        .maxCapacity(garage.maxCapacity())
                        .occupied(0).build();
                if (!garageSectorRepository.existsBySector(garage.sector())) {
                    garageSectorRepository.save(sector);
                }
            });

            response.spots().forEach(spot -> {
                Spot newSpot = Spot.builder()
                        .id(spot.id())
                        .sector(spot.sector())
                        .lat(spot.lat())
                        .lng(spot.lng())
                        .occupied(false)
                        .build();
                spotRepository.save(newSpot);
            });

            log.info("Garagem carregada com sucesso");

        } catch (Exception e) {
        log.error("Erro ao carregar garagem", e);
        }
    }
}
