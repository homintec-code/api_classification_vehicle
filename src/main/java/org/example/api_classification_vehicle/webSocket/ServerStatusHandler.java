package org.example.api_classification_vehicle.webSocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerStatusHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public ServerStatusHandler() {}


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        sendStatusUpdate("SERVER_UP"); // Envoi initial
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void sendStatusUpdate(String status) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(status));
                }
            } catch (IOException e) {
                // Gérer l'erreur
            }
        });
    }

    // Méthode pour vérifier l'état du serveur
    public void checkServerStatus() {
        boolean isServerUp = checkActualServerStatus(); // Implémentez cette méthode
        sendStatusUpdate(isServerUp ? "SERVER_UP" : "SERVER_DOWN");
    }

    private boolean checkActualServerStatus() {
        // Implémentez votre logique de vérification réelle ici
        // Par exemple: vérification DB, connexions externes, etc.
        return true;
    }
}