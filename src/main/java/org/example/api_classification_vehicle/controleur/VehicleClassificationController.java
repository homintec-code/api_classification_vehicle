package org.example.api_classification_vehicle.controleur;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.api_classification_vehicle.dto.VehicleClassificationDto;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.service.VehicleClassificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classifications")
@Tag(name = "Vehicle Classification", description = "API for vehicle classifications")
@CrossOrigin(origins = "http://localhost:8080")
public class VehicleClassificationController {

    private final VehicleClassificationService vehicleService;

    public VehicleClassificationController(VehicleClassificationService vehicleService) {
        this.vehicleService = vehicleService;
    }


    @PostMapping
    @Operation(summary = "Create a new vehicle classification")
    public VehicleClassification save(@RequestBody VehicleClassificationDto classification) {
        return vehicleService.save(classification);
    }

    @GetMapping
    public List<VehicleClassification> getAll() {
        return vehicleService.findAll();
    }
}

