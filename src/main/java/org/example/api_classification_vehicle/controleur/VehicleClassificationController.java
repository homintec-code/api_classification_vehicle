package org.example.api_classification_vehicle.controleur;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.api_classification_vehicle.dto.PageResponse;
import org.example.api_classification_vehicle.dto.VehicleClassificationDataDto;
import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.dto.VehicleStatsDto;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.service.VehicleClassificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/classifications")
@Tag(name = "Vehicle Classification", description = "API for vehicle classifications")
@CrossOrigin(origins = "http://localhost:4200")
public class VehicleClassificationController {

    private final VehicleClassificationService vehicleClassificationService;

    public VehicleClassificationController(VehicleClassificationService vehicleClassificationService) {
        this.vehicleClassificationService = vehicleClassificationService;
    }


    @PostMapping
    @Operation(summary = "Create a new vehicle classification")
    public VehicleClassification save(@RequestBody VehicleClassificationDto classification) {
        return vehicleClassificationService.save(classification);
    }

    @GetMapping
    public ResponseEntity<PageResponse<VehicleClassification>> getAllVehicles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false,defaultValue = "") String vehicleType,
            @RequestParam(required = false,defaultValue = "") String device,
            @RequestParam(required = false,defaultValue = "0") Integer axleCount,
            @RequestParam(required = false,defaultValue = "0") Integer tarrif,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int pageNumber = page <= 0 ? 0 : page - 1;

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(sortDirection, sort));

        Page<VehicleClassification> vehiclePage;

        // Vérifier si des filtres sont utilisés
        boolean hasFilters =
                isNotBlank(vehicleType) ||
                        isNotBlank(device) ||
                        isNotNull(axleCount) ||
                        isNotNull(tarrif);

        // Appliquer les filtres si présents
        if (hasFilters) {

            vehiclePage = vehicleClassificationService.filterWithSpecifications(
                    vehicleType, device, axleCount, tarrif, startDate, endDate, pageNumber, size);
        } else {
            vehiclePage = vehicleClassificationService.findAll(pageable);
        }

        PageResponse<VehicleClassification> response = new PageResponse<>(vehiclePage);
        return ResponseEntity.ok(response);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isNotNull(Integer value) {
        return value != null;
    }



    @GetMapping("last")
    public Optional<VehicleClassification> findone() {
        return vehicleClassificationService.findLastVehicle();
    }



/*    @GetMapping("/next-untreated/{device}")
    public ResponseEntity<VehicleClassification> getNextUntreateds(
            @PathVariable() String device

    ) {
        return vehicleClassificationService.findFirstUntreatedClassification(device)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }*/

    @GetMapping("/next-untreated/{device}")
    public ResponseEntity<VehicleClassificationDataDto> getNextUntreated(
            @PathVariable String device) {
        return vehicleClassificationService
                .findFirstUntreatedClassification(device)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Version alternative avec retour de l'objet modifié
    @PutMapping("/{id}/mark-treated")
    public ResponseEntity<VehicleClassification> markAsTreated(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleClassificationService.markAsTreatedAndReturn(id));
    }


    @PutMapping("/{device}/vehicle-classifications/mark-all-treatedx")
    public ResponseEntity<List<VehicleClassification>> markAllAsTreated(@PathVariable String device) {
        List<VehicleClassification> treatedClassifications = vehicleClassificationService.markAllAsTreatedAndReturnAll(device);
        return ResponseEntity.ok(treatedClassifications);
    }


    // Endpoint to trigger update for vehicle classification
    @PutMapping("/{device}/vehicle-classifications/mark-all-treated")
    public ResponseEntity<String> updateStatus(@PathVariable String device) {
        try {
            vehicleClassificationService.updateVehicleStatus(device);
            return ResponseEntity.ok("Vehicle status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating vehicle status.");
        }
    }

    @PutMapping("/{id}/mark-as-treated")
    public ResponseEntity<Void> markVehicleAsTreated(@PathVariable UUID id) {
        vehicleClassificationService.markAsTreated(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vehicles-by-category")
    public List<VehicleStatsDto> getVehicleCountByCategory() {
        return vehicleClassificationService.getVehicleStatsByCategory();
    }


    @GetMapping("/distinct-vehicle-classes")
    public ResponseEntity<List<String>> getDistinctVehicleClasses() {
        return ResponseEntity.ok(vehicleClassificationService.getDistinctVehicleClasses());
    }
}

