package org.example.api_classification_vehicle.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportTotals {

    private int totalClassifications;
    private int globalAvgTariff;
}
