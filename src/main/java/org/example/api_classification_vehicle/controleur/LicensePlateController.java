package org.example.api_classification_vehicle.controleur;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.api_classification_vehicle.dto.*;
import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.service.LicensePlateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/license-plate")
@Tag(name = "License plate", description = "API for License plat")
@CrossOrigin(origins = "http://localhost:4200")
public class LicensePlateController {

    private final LicensePlateService licensePlateService;

    public LicensePlateController(LicensePlateService licensePlateService) {
        this.licensePlateService = licensePlateService;
    }


    @PostMapping
    @Operation(summary = "Create a new  license Plate")
    public LicensePlate save(@RequestBody LicensePlateDto licensePlateDto) {
        return licensePlateService.save(licensePlateDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<LicensePlate>> getAllVehicles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String registrationNumber,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Convertir le numéro de page (1-based) en index (0-based)
        int pageNumber = page <= 0 ? 0 : page - 1;

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(sortDirection, sort));

        Page<LicensePlate> vehiclePage;

        if (registrationNumber != null || device != null || startDate != null && endDate != null ) {
            vehiclePage = licensePlateService.findFilteredLicensePlates(registrationNumber,device, startDate, endDate,pageable);
        } else {
            vehiclePage = licensePlateService.findAll(pageable);
        }

        PageResponse<LicensePlate> response = new PageResponse<>(vehiclePage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("last")
    public Optional<LicensePlate> findone() {
        return licensePlateService.findLastVehicle();
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
    public ResponseEntity<LicensePlate> getNextUntreated(
            @PathVariable String device) {
        return licensePlateService
                .findFirstUntreatedLicensePlate(device)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Version alternative avec retour de l'objet modifié
    @PutMapping("/{id}/mark-treated")
    public ResponseEntity<LicensePlate> markAsTreated(@PathVariable UUID id) {
        return ResponseEntity.ok(licensePlateService.markAsTreatedAndReturn(id));
    }


    @PutMapping("/{device}/vehicle-classifications/mark-all-treated")
    public ResponseEntity<List<LicensePlate>> markAllAsTreated(@PathVariable String device) {
        List<LicensePlate> LicensePlates = licensePlateService.markAllAsTreatedAndReturnAll(device);
        return ResponseEntity.ok(LicensePlates);
    }


    @PutMapping("/{id}/mark-as-treated")
    public ResponseEntity<Void> markVehicleAsTreated(@PathVariable UUID id) {
        licensePlateService.markAsTreated(id);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/local")
    public ResponseEntity<String> getPlate(@RequestParam String url) {
        try {
            String result = licensePlateService.getLicensePlate(url);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Failed to fetch plate data: " + e.getMessage());
        }
    }

}

