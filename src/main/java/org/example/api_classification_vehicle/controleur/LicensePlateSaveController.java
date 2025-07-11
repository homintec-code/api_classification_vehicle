package org.example.api_classification_vehicle.controleur;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.api_classification_vehicle.dto.LicensePlateDto;
import org.example.api_classification_vehicle.dto.PageResponse;
import org.example.api_classification_vehicle.model.LicensePlate;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/license-plate")
@Tag(name = "License plate", description = "API for License plat")
@CrossOrigin(origins = "http://localhost:4200")
public class LicensePlateSaveController {

    private final LicensePlateService licensePlateService;

    public LicensePlateSaveController(LicensePlateService licensePlateService) {
        this.licensePlateService = licensePlateService;
    }


    @PostMapping("save")
    @Operation(summary = "Create a new  license Plate")
    public LicensePlate save(@RequestBody LicensePlateDto licensePlateDto) {
        return licensePlateService.save(licensePlateDto);
    }


}

