package org.example.api_classification_vehicle.auth.dto;

import lombok.Data;
import org.example.api_classification_vehicle.auth.model.AppUser;

@Data
public class SigninResponseDto {
    private String accessToken;
    private AppUser user;

    public SigninResponseDto(String accessToken, AppUser user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
