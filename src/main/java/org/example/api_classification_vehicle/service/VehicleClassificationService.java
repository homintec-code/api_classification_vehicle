package org.example.api_classification_vehicle.service;


import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleClassificationService {

    private final VehicleClassificationRepository vehicleRepository;

    private final VehicleClassificationRepository vehicleClassificationRepository;


    @Autowired
    public VehicleClassificationService(VehicleClassificationRepository vehicleRepository, VehicleClassificationRepository vehicleClassificationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleClassificationRepository = vehicleClassificationRepository;
    }

    public VehicleClassification save(VehicleClassificationDto classification) {

        VehicleClassification vehicleClassification = new VehicleClassification();
        vehicleClassification.setVehicleType(classification.getVehicleType());
        vehicleClassification.setAxleCount(classification.getAxleCount());
        vehicleClassification.setVehicleClass(classification.getVehicleClass());
        vehicleClassification.setTarrif(classification.getTarrif());
        vehicleClassification.setImageBase64(classification.getImageBase64());

        return vehicleClassificationRepository.save(vehicleClassification);
    }

    public List<VehicleClassification> findAll() {
        return vehicleRepository.findAll();
    }
}
