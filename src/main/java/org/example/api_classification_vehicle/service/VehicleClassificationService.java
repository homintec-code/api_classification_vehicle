package org.example.api_classification_vehicle.service;


import jakarta.transaction.Transactional;
import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.example.api_classification_vehicle.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VehicleClassificationService {

    private final VehicleClassificationRepository vehicleClassificationRepository;


    @Autowired
    public VehicleClassificationService(VehicleClassificationRepository vehicleClassificationRepository) {
        this.vehicleClassificationRepository = vehicleClassificationRepository;
    }

    public VehicleClassification save(VehicleClassificationDto classification) {

        VehicleClassification vehicleClassification = new VehicleClassification();
        vehicleClassification.setVehicleType(classification.getVehicleType());
        vehicleClassification.setAxleCount(classification.getAxleCount());
        vehicleClassification.setVehicleClass(classification.getVehicleClass());
        vehicleClassification.setTarrif(classification.getTarrif());
        vehicleClassification.setImageBase64(classification.getImageBase64());
        vehicleClassification.setDevice(classification.getDevice());
        return vehicleClassificationRepository.save(vehicleClassification);
    }

    public List<VehicleClassification> findAll() {
        return vehicleClassificationRepository.findAll();
    }




    public Optional<VehicleClassification> findFirstUntreatedClassification() {
        // Méthode 1: Directement via le repository
        return vehicleClassificationRepository.findFirstByToTreatFalse();
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

}
