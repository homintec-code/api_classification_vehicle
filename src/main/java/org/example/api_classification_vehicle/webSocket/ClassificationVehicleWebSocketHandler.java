package org.example.api_classification_vehicle.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClassificationVehicleWebSocketHandler  extends TextWebSocketHandler  {



    private final Map<String, WebSocketSession> connectedClients = new ConcurrentHashMap<>();
    private final WebSocketService webSocketService;



    public ClassificationVehicleWebSocketHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    // Handle new WebSocket connection
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientIp = session.getRemoteAddress().getAddress().getHostAddress();
        String clientId = session.getId();
        String uniqueClientId = clientIp + "-" + clientId;
        // connectedClients.put(uniqueClientId, session);
        webSocketService.addClient(session, uniqueClientId);
        System.out.println("Client connected: " + uniqueClientId);
    }

    // Handle WebSocket disconnection
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientIp = session.getRemoteAddress().getAddress().getHostAddress();
        String clientId = session.getId();
        String uniqueClientId = clientIp + "-" + clientId;
        //connectedClients.remove(uniqueClientId);
        webSocketService.removeClient(uniqueClientId);

        System.out.println("Client disconnected: " + uniqueClientId);
    }
}
