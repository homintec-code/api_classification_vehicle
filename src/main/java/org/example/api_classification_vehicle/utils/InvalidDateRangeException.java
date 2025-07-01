package org.example.api_classification_vehicle.utils;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public InvalidDateRangeException(String message, LocalDate startDate, LocalDate endDate) {
        super(message);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters pour les dates
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Méthode pour les détails techniques
    public String getDetails() {
        return String.format("Plage invalide: %s à %s", startDate, endDate);
    }
}