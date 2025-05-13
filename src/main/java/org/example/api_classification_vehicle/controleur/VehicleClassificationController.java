package org.example.api_classification_vehicle.controleur;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.service.VehicleClassificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classifications")
@Tag(name = "Vehicle Classification", description = "API for vehicle classifications")
@CrossOrigin(origins = "http://localhost:8080")
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
    public List<VehicleClassification> getAll() {
        return vehicleClassificationService.findAll();
    }


    @GetMapping("/next-untreated")
    public ResponseEntity<VehicleClassification> getNextUntreated() {
        return vehicleClassificationService.findFirstUntreatedClassification()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // Version alternative avec retour de l'objet modifié
    @PutMapping("/{id}/mark-treated")
    public ResponseEntity<VehicleClassification> markAsTreated(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleClassificationService.markAsTreatedAndReturn(id));
    }


    @PutMapping("/{id}/mark-as-treated")
    public ResponseEntity<Void> markVehicleAsTreated(@PathVariable UUID id) {
        vehicleClassificationService.markAsTreated(id);
        return ResponseEntity.ok().build();
    }


}

