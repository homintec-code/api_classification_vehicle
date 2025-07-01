package org.example.api_classification_vehicle.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.example.api_classification_vehicle.utils.ImageResizer;

import java.text.Normalizer;
import java.util.Date;

@Data
public class VehicleClassificationDataDto {


    private String id;
    private String vehicleType; // Exemple : Camion, Voiture, Bus,MINBUS etc.

    private int axleCount; // Nombre d'essieux, Number of axles

    private String vehicleClass; //  Exemple : VEHICULE LEGER, MIMIBBUS DE 9 A 15 PLACE

    private double tarrif;

    private String imageBase64;

    private String device;




    // Getters et setters
    public String getImageBase64() {
        return this.imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }



    private Date createdAt;

    private Date updatedAt;


}
