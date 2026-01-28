package org.example.api_classification_vehicle.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ClassificationVehicleWebSocketHandler classificationVehicleWebSocketHandler;
    private final LicensePlateWebSocketHandler licensePlateWebSocketHandler;

    public WebSocketConfig(ClassificationVehicleWebSocketHandler classificationVehicleWebSocketHandler, LicensePlateWebSocketHandler licensePlateWebSocketHandler) {
        this.classificationVehicleWebSocketHandler = classificationVehicleWebSocketHandler;
        this.licensePlateWebSocketHandler = licensePlateWebSocketHandler;
    }


    @Bean
    public ServerStatusHandler serverStatusHandler() {
        return new ServerStatusHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(classificationVehicleWebSocketHandler, "/api/vehicle-classifications")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler(
                        new TomcatRequestUpgradeStrategy()))
                .addInterceptors(new HttpSessionHandshakeInterceptor()); // Allow all origins

        registry.addHandler(licensePlateWebSocketHandler, "/api/v1/license-plate")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler(
                        new TomcatRequestUpgradeStrategy()))
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        registry.addHandler(serverStatusHandler(), "/ws-status")
                .setAllowedOrigins("*");

    }

}