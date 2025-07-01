package org.example.api_classification_vehicle.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.api_classification_vehicle.Audit;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "license_plate")
@Data
@EntityListeners(AuditingEntityListener.class) // Add this
public class LicensePlate extends Audit {

    private String registration_number; // Exemple : BP9867BR.

    @Lob  // For large text data (BASE64 images can be long)
    @Column(name = "image_plate", columnDefinition = "TEXT")
    private String imagePlate64;

    @Lob  // For large text data (BASE64 images can be long)
    @Column(name = "image_vehicle_base64", columnDefinition = "TEXT")
    private String imageVehicleBase64;

    @Column(name = "to_treat", nullable = false)
    private boolean toTreat = false;

    private String device; //Exemple : Camera1, Camera2, Camera3


}
