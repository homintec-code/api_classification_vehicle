package org.example.api_classification_vehicle.events;

import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.context.ApplicationEvent;

public class ClassificationVehicleCreatedEvent extends ApplicationEvent {

    public ClassificationVehicleCreatedEvent(VehicleClassification source) {
        super(source);
    }

    @Override
    public VehicleClassification getSource() {
        return (VehicleClassification) super.getSource();
    }
}