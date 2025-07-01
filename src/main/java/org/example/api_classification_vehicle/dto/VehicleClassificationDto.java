package org.example.api_classification_vehicle.dto;

import lombok.Data;

import java.util.Date;

@Data
public class VehicleClassificationDto {


    private String vehicleType; // Exemple : Camion, Voiture, Bus,MINBUS etc.

    private int axleCount; // Nombre d'essieux, Number of axles

    private String vehicleClass; //  Exemple : VEHICULE LEGER, MIMIBBUS DE 9 A 15 PLACE

    private double tarrif;

    private String imageBase64;

    private String device;

    // Getter normal
    public String getVehicleClass() {
        return this.vehicleClass != null ? this.vehicleClass.toUpperCase() : null;
    }

}

