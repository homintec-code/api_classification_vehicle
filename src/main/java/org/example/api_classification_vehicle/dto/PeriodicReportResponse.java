package org.example.api_classification_vehicle.dto;

import java.util.List;

// DTO API
public record PeriodicReportResponse(
        String periodType,
        List<ReportItemResponse> items,
        TotalsResponse totals
) {}