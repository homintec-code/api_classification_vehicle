package org.example.api_classification_vehicle.model;

import org.example.api_classification_vehicle.dto.ClassificationProjection;

import java.time.LocalDate;
import java.util.List;

public record PeriodicReport(
        List<ReportItem> items,
        ReportTotals totals
) {}