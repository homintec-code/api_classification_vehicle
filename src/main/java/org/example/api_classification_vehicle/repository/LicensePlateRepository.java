package org.example.api_classification_vehicle.repository;

import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicensePlateRepository extends JpaRepository<LicensePlate, UUID>, JpaSpecificationExecutor<LicensePlate> {
    // custom query methods if needed


    // Trouver le premier véhicule non traité (toTreat = true)
    Optional<LicensePlate> findFirstByToTreatTrue();

    Optional<LicensePlate> findFirstByToTreatFalse();

    // Alternative: trouver tous les véhicules non traités et prendre le premier
    List<LicensePlate> findByToTreatTrue();


    // Pour les rapports (sans pagination)
    List<LicensePlate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Pour la pagination
    Page<LicensePlate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("""
    SELECT l FROM LicensePlate l
    WHERE 
        (:registrationNumber IS NULL OR l.registration_number = :registrationNumber)
        OR (:device IS NULL OR l.device = :device)
        OR (:startDate IS NULL OR :endDate IS NULL OR l.createdAt BETWEEN :startDate AND :endDate)
""")
    Page<LicensePlate> findByOptionalRegistrationOrDeviceOrCreatedAtBetween(
            @Param("registrationNumber") String registrationNumber,
            @Param("device") String device,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    @Query("""
    SELECT l FROM LicensePlate l
    WHERE 
        (:registrationNumber IS NULL OR l.registration_number = :registrationNumber)
        OR (:device IS NULL OR l.device = :device)
        OR (:startDate IS NULL OR :endDate IS NULL OR l.createdAt BETWEEN :startDate AND :endDate)
""")
    List<LicensePlate> findByOptionalRegistrationOptionalOrDeviceOrCreatedAtBetween(
            @Param("registrationNumber") String registrationNumber,
            @Param("device") String device,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    // Repository
    @Query("SELECT v FROM LicensePlate v ORDER BY v.createdAt DESC LIMIT 1")
    Optional<LicensePlate> findLastVehicle();


    Optional<LicensePlate> findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(String device);


    @Query("SELECT lp FROM LicensePlate lp WHERE lp.toTreat = false AND lp.device = :device")
    List<LicensePlate> findAllByToTreatFalseAndDevice(@Param("device") String device);



    List<LicensePlate> findByDevice(String device);

    public interface VehicleStats {
        String getCategory();
        Long getCount();
    }
}