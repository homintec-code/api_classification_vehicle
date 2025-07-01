package org.example.api_classification_vehicle.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {


    private final Map<String, WebSocketSession> connectedClients = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    public void broadcastMessage(String message) {
        for (WebSocketSession session : connectedClients.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addClient(WebSocketSession session, String uniqueClientId) {
        connectedClients.put(uniqueClientId, session);
    }

    public void removeClient(String uniqueClientId) {
        connectedClients.remove(uniqueClientId);
    }


    public void sendClassificationDetails(VehicleClassification classification) {
        try {
            String json = objectMapper.writeValueAsString(classification);
            broadcastMessage(json);
        } catch (Exception e) {
            System.err.println("Error serializing classification: " + e.getMessage());
        }
    }

        public void sendLicensePlateDetails(LicensePlate licensePlate) {
        try {
            String json = objectMapper.writeValueAsString(licensePlate);
            broadcastMessage(json);
        } catch (Exception e) {
            System.err.println("Error serializing license plate: " + e.getMessage());
        }
    }




}

