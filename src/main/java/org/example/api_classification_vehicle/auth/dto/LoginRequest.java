package org.example.api_classification_vehicle.auth.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginRequest {
    private String username;
    private String password;
    // getters & setters
}

