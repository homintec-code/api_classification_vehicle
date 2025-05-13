package org.example.api_classification_vehicle.repository;

import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleClassificationRepository extends JpaRepository<VehicleClassification, UUID> {
    // custom query methods if needed


    // Trouver le premier véhicule non traité (toTreat = true)
    Optional<VehicleClassification> findFirstByToTreatTrue();

    Optional<VehicleClassification> findFirstByToTreatFalse();

    // Alternative: trouver tous les véhicules non traités et prendre le premier
    List<VehicleClassification> findByToTreatTrue();
}