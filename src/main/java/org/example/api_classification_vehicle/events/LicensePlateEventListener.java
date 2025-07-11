package org.example.api_classification_vehicle.events;

import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.service.LicensePlateService;
import org.example.api_classification_vehicle.service.VehicleClassificationService;
import org.example.api_classification_vehicle.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LicensePlateEventListener {

    private final WebSocketService webSocketService;

    @Autowired
    public LicensePlateEventListener(LicensePlateService licensePlateService, WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleValidationCreatedEvent(LicensePlateCreatedEvent event) {
        LicensePlate licensePlate = (LicensePlate) event.getSource();
        // Traitez l'événement ici
        webSocketService.sendLicensePlateDetails(licensePlate);
        System.out.println("Nouvelle plaque créée - ID: " + licensePlate.getId() +
                ", Type: " + licensePlate.getRegistration_number() );
        //sendValidationToClients(event);
    }

}