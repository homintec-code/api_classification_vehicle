package org.example.api_classification_vehicle.model;


public class VehicleTypeStats {
    private long count;
    private double totalTariff;

    public void add(long additionalCount, double additionalAvgTariff) {
        this.count += additionalCount;
        this.totalTariff += additionalAvgTariff * additionalCount;
    }

    // Getters
    public long getCount() {
        return count;
    }

    public double getAvgTariff() {
        return count > 0 ? totalTariff / count : 0;
    }
}