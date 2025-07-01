package org.example.api_classification_vehicle.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.api_classification_vehicle.Audit;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "vehicle_classifications")
@Data
@EntityListeners(AuditingEntityListener.class) // Add this
public class VehicleClassification extends Audit {

    private String vehicleType; // Exemple : Camion, Voiture, Bus,MINBUS etc.

    private int axleCount; // Nombre d'essieux, Number of axles

    private String vehicleClass; //  Exemple : VEHICULE LEGER, MIMIBBUS DE 9 A 15 PLACE

    private double tarrif;

    @Lob  // For large text data (BASE64 images can be long)
    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;

    @Column(name = "to_treat", nullable = false)
    private boolean toTreat = false;

    private String device; //Exemple : Camera1, Camera2, Camera3


}
