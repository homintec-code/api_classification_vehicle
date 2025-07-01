package org.example.api_classification_vehicle.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LicensePlateDataDto {


    private String id;
    private String registration_number; //

    private String imagePlate64; //

    private String imageVehicleBase64;

    private String device;

    private Date createdAt;

    private Date updatedAt;


}
