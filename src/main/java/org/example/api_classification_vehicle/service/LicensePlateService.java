package org.example.api_classification_vehicle.service;


import jakarta.transaction.Transactional;
import org.example.api_classification_vehicle.dto.*;
import org.example.api_classification_vehicle.events.LicensePlateCreatedEvent;
import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.repository.LicensePlateRepository;
import org.example.api_classification_vehicle.utils.ImageResizer;
import org.example.api_classification_vehicle.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class LicensePlateService {


    @Autowired
    private RestTemplate restTemplate;

    private final LicensePlateRepository licensePlateRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public LicensePlateService(LicensePlateRepository licensePlateRepository, ApplicationEventPublisher applicationEventPublisher,RestTemplate restTemplate) {
        this.licensePlateRepository = licensePlateRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.restTemplate = restTemplate;
    }

    public LicensePlate save(LicensePlateDto licensePlateDto) {

        LicensePlate licensePlate = new LicensePlate();
        licensePlate.setRegistration_number(licensePlate.getRegistration_number());
        licensePlate.setImagePlate64(licensePlateDto.getImagePlate64());
        licensePlate.setImageVehicleBase64(licensePlateDto.getImageVehicleBase64());
        licensePlate.setRegistration_number(licensePlate.getRegistration_number());
        licensePlate.setDevice(licensePlate.getDevice());
        LicensePlate saved = licensePlateRepository.save(licensePlate);
        // Envoi de la notification via WebSocket

        applicationEventPublisher.publishEvent(new LicensePlateCreatedEvent(saved));

        return saved;
    }


    // Paginated find all
    public Page<LicensePlate> findAll(Pageable pageable) {
        return (Page<LicensePlate>) licensePlateRepository.findAll(pageable);
    }

    // Or if you specifically need List:
    public List<LicensePlate> findAllAsList(Pageable pageable) {
        return (List<LicensePlate>) licensePlateRepository.findAll(pageable).getContent();
    }

    private Sort.Order[] parseSort(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Multiple sort criteria
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // Single sort criteria
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders.toArray(new Sort.Order[0]);
    }

    private Sort.Direction getSortDirection(String direction) {
        return direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    public Optional<LicensePlate> findLastVehicle() {
        return licensePlateRepository.findLastVehicle();
    }



    public Optional<LicensePlate> findFirstUntreatedLicensePlate(String device ) {
        // Méthode 1: Directement via le repository
       // return vehicleClassificationRepository.findFirstByToTreatFalse();

        return licensePlateRepository.findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(device);

    }

    public Optional<LicensePlateDataDto> findFirstUntreatedLicennsePlate(String device) {
        return licensePlateRepository
                .findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(device)
                .map(entity -> {
                    // Conversion vers DTO
                    LicensePlateDataDto dto = new LicensePlateDataDto();
                    dto.setId(entity.getId().toString());
                    dto.setRegistration_number(entity.getRegistration_number());
                    dto.setImagePlate64(entity.getImagePlate64());
                    dto.setImageVehicleBase64(entity.getImageVehicleBase64());
                    dto.setDevice(entity.getDevice());
                    dto.setCreatedAt(entity.getCreatedAt());
                    dto.setUpdatedAt(entity.getUpdatedAt());
                    // ... autres champs ...

                    // Redimensionnement de l'image si elle existe
                    if (entity.getImagePlate64() != null && !entity.getImagePlate64().isEmpty()) {
                        String resizedImage = ImageResizer.resizeBase64Image(
                                entity.getImagePlate64(),
                                100,  // largeur max
                                100   // hauteur max
                        );
                        dto.setImagePlate64(resizedImage);
                    }
                    if (entity.getImageVehicleBase64() != null && !entity.getImageVehicleBase64().isEmpty()) {
                        String resizedImage = ImageResizer.resizeBase64Image(
                                entity.getImageVehicleBase64(),
                                2300,  // largeur max
                                250   // hauteur max
                        );
                        dto.setImageVehicleBase64(resizedImage);
                    }

                    return dto;
                });
    }

    private String removeAccentsAndUpperCase(String input) {
        if (input == null) return null;

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase();
    }

    public Optional<LicensePlate> findUntreatedClassification() {
        // Méthode 1: Directement via le repository
        // OU Méthode 2: Via la liste
        List<LicensePlate> untreated = licensePlateRepository.findByToTreatTrue();
        return untreated.isEmpty() ? Optional.empty() : Optional.of(untreated.get(0));
    }

    @Transactional
    public void markAsTreated(UUID id) {
        licensePlateRepository.findById(id).ifPresent(v -> {
            v.setToTreat(true);
            licensePlateRepository.save(v);
        });
    }

    // Version alternative qui retourne l'objet modifié
    @Transactional
    public LicensePlate markAsTreatedAndReturn(UUID id) {
        return licensePlateRepository.findById(id)
                .map(v -> {
                    v.setToTreat(true);
                    return licensePlateRepository.save(v);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle classification not found with id: " + id));
    }





    @Transactional
    public List<LicensePlate> markAllAsTreatedAndReturnAll(String device) {
        List<LicensePlate> LicensePlates = licensePlateRepository.findFirstByToTreatIsFalseAndDevice(device);
        LicensePlates.forEach(v -> v.setToTreat(true)); // Assuming false means "treated"
        return licensePlateRepository.saveAll(LicensePlates);
    }


    public Page<LicensePlate> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return licensePlateRepository.findByCreatedAtBetween(startDate, endDate, pageable);

    }

    public Page<LicensePlate> findByOptionalRegistrationOrDeviceOrCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate,String registrationNumber, String device, Pageable pageable) {
        return licensePlateRepository.findByOptionalRegistrationOrDeviceOrCreatedAtBetween(registrationNumber,device, startDate, endDate, pageable);

    }

    public List<Map<String, Object>> getLicensePlates(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ParameterizedTypeReference<List<Map<String, Object>>> responseType =
                new ParameterizedTypeReference<>() {};
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        );
        return response.getBody();
    }

    public String getLicensePlate(String url) {
        return restTemplate.getForObject(url, String.class);
    }

}
