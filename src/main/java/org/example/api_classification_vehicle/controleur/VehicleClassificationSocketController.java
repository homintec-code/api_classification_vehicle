package org.example.api_classification_vehicle.controleur;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class VehicleClassificationSocketController {

    @MessageMapping("/save-vehicle")
    @SendTo("/topic/new-classification")
    public String handleSaveNotification(String message) {
        return "Nouvelle classification sauvegardée: " + message;
    }
}