package org.example.api_classification_vehicle.service;


import jakarta.transaction.Transactional;
import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.dto.VehicleClassificationDataDto;
import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.dto.VehicleStatsDto;
import org.example.api_classification_vehicle.events.ClassificationVehicleCreatedEvent;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.example.api_classification_vehicle.utils.ImageResizer;
import org.example.api_classification_vehicle.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleClassificationService {

    private final VehicleClassificationRepository vehicleClassificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public VehicleClassificationService(VehicleClassificationRepository vehicleClassificationRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.vehicleClassificationRepository = vehicleClassificationRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public VehicleClassification save(VehicleClassificationDto classification) {

        VehicleClassification vehicleClassification = new VehicleClassification();
        vehicleClassification.setVehicleType(classification.getVehicleType());
        vehicleClassification.setAxleCount(classification.getAxleCount());
        vehicleClassification.setVehicleClass(classification.getVehicleClass());
        vehicleClassification.setTarrif(classification.getTarrif());
        vehicleClassification.setImageBase64(classification.getImageBase64());
        vehicleClassification.setDevice(classification.getDevice());
        VehicleClassification saved = vehicleClassificationRepository.save(vehicleClassification);
        // Envoi de la notification via WebSocket

        applicationEventPublisher.publishEvent(new ClassificationVehicleCreatedEvent(saved));

        /// messagingTemplate.convertAndSend(

              ///  "/topic/new-classification",
              /// "Classification sauvegardée - ID: " + saved.getId()
        ///);

        return saved;
    }


    // Paginated find all
    public Page<VehicleClassification> findAll(Pageable pageable) {
        return (Page<VehicleClassification>) vehicleClassificationRepository.findAll(pageable);
    }

    // Or if you specifically need List:
    public List<VehicleClassification> findAllAsList(Pageable pageable) {
        return (List<VehicleClassification>) vehicleClassificationRepository.findAll(pageable).getContent();
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

    public Optional<VehicleClassification> findLastVehicle() {
        return vehicleClassificationRepository.findLastVehicle();
    }



    public Optional<VehicleClassification> findFirstUntreatedClassifications(String device ) {
        // Méthode 1: Directement via le repository
       // return vehicleClassificationRepository.findFirstByToTreatFalse();

        return vehicleClassificationRepository.findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(device);

    }

    public Optional<VehicleClassificationDataDto> findFirstUntreatedClassification(String device) {
        return vehicleClassificationRepository
                .findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(device)
                .map(entity -> {
                    // Conversion vers DTO
                    VehicleClassificationDataDto dto = new VehicleClassificationDataDto();
                    dto.setId(entity.getId().toString());
                    dto.setVehicleClass( removeAccentsAndUpperCase(entity.getVehicleClass().toUpperCase()));
                    dto.setVehicleType(entity.getVehicleType());
                    dto.setAxleCount(entity.getAxleCount());
                    dto.setTarrif(entity.getTarrif());
                    dto.setDevice(entity.getDevice());
                    dto.setCreatedAt(entity.getCreatedAt());
                    dto.setUpdatedAt(entity.getUpdatedAt());




                    // ... autres champs ...

                    // Redimensionnement de l'image si elle existe
                    if (entity.getImageBase64() != null && !entity.getImageBase64().isEmpty()) {
                        String resizedImage = ImageResizer.resizeBase64Image(
                                entity.getImageBase64(),
                                2300,  // largeur max
                                250   // hauteur max
                        );
                        dto.setImageBase64(resizedImage);
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

    public Optional<VehicleClassification> findUntreatedClassification() {
        // Méthode 1: Directement via le repository
        // OU Méthode 2: Via la liste
        List<VehicleClassification> untreated = vehicleClassificationRepository.findByToTreatTrue();
        return untreated.isEmpty() ? Optional.empty() : Optional.of(untreated.get(0));
    }

    @Transactional
    public void markAsTreated(UUID id) {
        vehicleClassificationRepository.findById(id).ifPresent(v -> {
            v.setToTreat(true);
            vehicleClassificationRepository.save(v);
        });
    }

    // Version alternative qui retourne l'objet modifié
    @Transactional
    public VehicleClassification markAsTreatedAndReturn(UUID id) {
        return vehicleClassificationRepository.findById(id)
                .map(v -> {
                    v.setToTreat(true);
                    return vehicleClassificationRepository.save(v);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle classification not found with id: " + id));
    }





    @Transactional
    public List<VehicleClassification> markAllAsTreatedAndReturnAll(String device) {
        List<VehicleClassification> classifications = vehicleClassificationRepository.findFirstByToTreatIsFalseAndDevice(device);

        System.out.print(classifications);
        classifications.forEach(v -> v.setToTreat(true)); // Assuming false means "treated"
        return vehicleClassificationRepository.saveAll(classifications);
    }


    public void updateVehicleStatus(String device) {
        vehicleClassificationRepository.updateStatus(device);
    }

    public List<VehicleStatsDto> getVehicleStatsByCategory() {
        return vehicleClassificationRepository.countByCategory().stream()
                .map(stats -> new VehicleStatsDto(stats.getCategory(), stats.getCount()))
                .collect(Collectors.toList());
    }

  

    public Page<VehicleClassification> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return vehicleClassificationRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }


    public Page<VehicleClassification> findByOptionalVehicleTypeOrDeviceOrCreatedAtBetween(String vehicleType,String device,  int axleCount,int tarrif, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return vehicleClassificationRepository.findByOptionalVehicleTypeOrDeviceOrCreatedAtBetween(vehicleType,device, axleCount,tarrif,  startDate, endDate, pageable);
    }



}
