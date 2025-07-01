package org.example.api_classification_vehicle.events;

import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.context.ApplicationEvent;

public class LicensePlateCreatedEvent extends ApplicationEvent {

    public LicensePlateCreatedEvent(LicensePlate source) {
        super(source);
    }

    @Override
    public LicensePlate getSource() {
        return (LicensePlate) super.getSource();
    }
}