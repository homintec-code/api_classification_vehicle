package org.example.api_classification_vehicle.dto;

import lombok.Data;

@Data
public class VehicleStatsDto {
    private String category;
    private long count;

    // Constructeur
    public VehicleStatsDto(String category, long count) {
        this.category = category;
        this.count = count;
    }
}
