package org.example.api_classification_vehicle.dto;

import lombok.Data;

@Data
public class LicensePlateDto {


    private String registration_number; // Exemple :BP9867BRtc.

    private String imagePlate64; //  Exemple : VEHICULE LEGER, MIMIBBUS DE 9 A 15 PLACE

    private String imageVehicleBase64;


    private String device;


}

