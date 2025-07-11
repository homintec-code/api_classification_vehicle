package org.example.api_classification_vehicle.repository;

import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleClassificationRepository extends JpaRepository<VehicleClassification, UUID> , JpaSpecificationExecutor<VehicleClassification> {
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


    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.vehicleClass = :vehicleType OR " +
            "v.device = :device OR " +
            "v.axleCount = :axleCount OR " +
            "v.tarrif = :tarrif OR " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicle(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.vehicleClass = :vehicleType AND " +
            "v.device = :device AND " +
            "v.axleCount = :axleCount AND " +
            "v.tarrif = :tarrif AND " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicleAndVehicleTypeAndDeviceAndCreatedAtBetweenAndAxleCountAndTarrif(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.vehicleClass = :vehicleType AND " +
            "v.device = :device AND " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicleAndVehicleType(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.axleCount = :axleCount AND " +
            "v.tarrif = :tarrif AND " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicleAndAxleCountAndTarrif(
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.axleCount = :axleCount AND " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicleAndAxleCount(
            @Param("axleCount") Integer axleCount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "v.tarrif = :tarrif AND " +
            "v.createdAt BETWEEN :startDate AND :endDate")
    List<VehicleClassification> findByOptionalVehicleAndTarrif(
            @Param("tarrif") Integer tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





    @Query("""
    SELECT v FROM VehicleClassification v
    WHERE 
      (:vehicleType IS NULL OR v.vehicleClass = :vehicleType)
      OR (:device IS NULL OR v.device = :device)
      OR (:axleCount!=0  OR v.axleCount = :axleCount)
      OR (:tarrif !=0 OR v.tarrif = :tarrif)
         AND (
                (:startDate IS NULL OR :endDate IS NULL)\s
                OR (v.createdAt BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp))
            )
""")
    Page<VehicleClassification> findByOptionalVehicleTypeOrDeviceOrCreatedAtBetween(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    );




    @Query("""
SELECT v FROM VehicleClassification v
WHERE
(:vehicleType IS NULL OR v.vehicleClass = :vehicleType)
AND (:axleCount!=0 OR v.axleCount = :axleCount)
AND (:tarrif!=0 OR v.tarrif = :tarrif)
AND (:device IS NULL OR v.device = :device)
AND (
    (:startDate IS NULL OR :endDate IS NULL)
    OR v.createdAt BETWEEN :startDate AND :endDate
)
""")
    Page<VehicleClassification> filterAll(
            @Param("vehicleType") String vehicleType,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Double tarrif,
            @Param("device") String device,
            @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    );


    @Query("SELECT v FROM VehicleClassification v WHERE " +
            "(:vehicleType IS NULL OR v.vehicleClass = :vehicleType) AND " +
            "(:device IS NULL OR v.device = :device) AND " +
            "(:axleCount IS NULL OR v.axleCount = :axleCount) AND " +
            "(:tarrif IS NULL OR v.tarrif = :tarrif) AND " +
            "(:startDate IS NULL OR :endDate IS NULL OR v.createdAt BETWEEN :startDate AND :endDate)")
    Page<VehicleClassification> findByOptionalVehicleTypeOrDeviceOrCreatedAtBetweenOther(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
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



    @Query("""
    SELECT v FROM VehicleClassification v
    WHERE 
      (:vehicleType IS NULL OR v.vehicleType = :vehicleType)
      OR (:device IS NULL OR v.device = :device)
      OR (:axleCount IS NULL OR :axleCount = 0 OR v.axleCount = :axleCount)
      OR (:tarrif IS NULL OR :tarrif = 0 OR v.tarrif = :tarrif)
      OR (
          (:startDate IS NULL AND :endDate IS NULL)
          OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND 
              v.createdAt BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp))
      )
    ORDER BY v.id ASC
""")
    Page<VehicleClassification> findByOptionalFilters(
            @Param("vehicleType") String vehicleType,
            @Param("device") String device,
            @Param("axleCount") Integer axleCount,
            @Param("tarrif") Integer tarrif,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );



    @Query("SELECT DISTINCT v.vehicleClass FROM VehicleClassification v")
    List<String> findDistinctVehicleClasses();
}