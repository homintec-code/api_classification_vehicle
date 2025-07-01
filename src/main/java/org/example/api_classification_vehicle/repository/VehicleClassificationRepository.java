package org.example.api_classification_vehicle.repository;

import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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


    // Pour les rapports (sans pagination)
    List<VehicleClassification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Pour la pagination
    Page<VehicleClassification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Facultatif : filtrer aussi par date
    Page<VehicleClassification> findByVehicleTypeAndDeviceAndCreatedAtBetween(
            String vehicleType, String device, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );


    @Query("""
    SELECT v FROM VehicleClassification v
    WHERE 
      (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
      OR (:device IS NULL OR v.device = :device)
          OR (:axleCount IS NULL OR v.axleCount = :axleCount)
      OR (:tarrif IS NULL OR v.tarrif = :tarrif)
      OR (:startDate IS NULL OR :endDate IS NULL OR v.createdAt BETWEEN :startDate AND :endDate)
""")
    List<VehicleClassification> findByOptionalVehicle(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") int axleCount,
            @Param("tarrif") int tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query("""
    SELECT v FROM VehicleClassification v
    WHERE 
      (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
      OR (:device IS NULL OR v.device = :device)
      OR (:axleCount IS NULL OR v.axleCount = :axleCount)
      OR (:tarrif IS NULL OR v.tarrif = :tarrif)
      
      OR (:startDate IS NULL OR :endDate IS NULL OR v.createdAt BETWEEN :startDate AND :endDate)
""")
    Page<VehicleClassification> findByOptionalVehicleTypeOrDeviceOrCreatedAtBetween(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") int axleCount,
            @Param("tarrif") int tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );




    @Query("SELECT v.vehicleClass as category, COUNT(v) as count " +
            "FROM VehicleClassification v " +
            "GROUP BY v.vehicleClass")
    List<VehicleStats> countByCategory();

    // Repository
    @Query("SELECT v FROM VehicleClassification v ORDER BY v.createdAt DESC LIMIT 1")
    Optional<VehicleClassification> findLastVehicle();


    Optional<VehicleClassification> findFirstByToTreatIsFalseAndDeviceOrderByCreatedAtAsc(String device);



    List<VehicleClassification> findFirstByToTreatIsFalseAndDevice(String device);



    @Modifying
    @Query("UPDATE VehicleClassification v SET v.toTreat = true WHERE v.device = :device")
    void updateStatus(@Param("device") String device);



    List<VehicleClassification> findByDevice(String device);

    public interface VehicleStats {
        String getCategory();
        Long getCount();
    }
}