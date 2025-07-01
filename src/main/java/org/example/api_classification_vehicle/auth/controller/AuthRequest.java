package org.example.api_classification_vehicle.auth.controller;

// DTOs
public record AuthRequest(String username, String password) {
    public Object getUsername() {
        return username;
    }

    public Object getPassword() {
        return password;
    }
}
