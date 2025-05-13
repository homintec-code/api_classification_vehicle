package org.example.api_classification_vehicle.repository;

import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleClassificationRepository extends JpaRepository<VehicleClassification, UUID> {
    // custom query methods if needed
}