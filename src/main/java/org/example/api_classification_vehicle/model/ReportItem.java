package org.example.api_classification_vehicle.model;
// ReportItem.java
import org.example.api_classification_vehicle.dto.ClassificationProjection;

import java.time.LocalDate;
import java.util.Map;

public record ReportItem(
        LocalDate period,
        Map<String, VehicleTypeStats> stats,
        Long totalCount,
        Double totalAvgTariff
) {

    public void addClassification(ClassificationProjection projection) {
        stats.computeIfAbsent(projection.getVehicleType(), k -> new VehicleTypeStats())
                .add(projection.getCount(), projection.getAvgTariff());
    }
}