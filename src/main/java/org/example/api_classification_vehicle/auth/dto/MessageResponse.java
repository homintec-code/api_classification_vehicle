package org.example.api_classification_vehicle.auth.dto;

import lombok.Data;

@Data
public class MessageResponse {
    private String message;
    // constructeur & getter
    public MessageResponse(String message) {}
}
