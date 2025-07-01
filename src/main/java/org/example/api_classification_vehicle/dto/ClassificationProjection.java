package org.example.api_classification_vehicle.dto;

import java.time.LocalDate;

public interface ClassificationProjection {
    LocalDate getDay();
    String getVehicleType();
    Long getCount();
    Double getAvgTariff();
}
