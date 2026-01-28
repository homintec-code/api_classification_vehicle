package org.example.api_classification_vehicle.events;

import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.webSocket.SocketLicensePlateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LicensePlateEventListener {

    private final SocketLicensePlateService socketLicensePlateService;

    @Autowired
    public LicensePlateEventListener(SocketLicensePlateService socketLicensePlateService) {
        this.socketLicensePlateService = socketLicensePlateService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleValidationCreatedEvent(LicensePlateCreatedEvent event) {
        LicensePlate licensePlate = (LicensePlate) event.getSource();
        // Traitez l'événement ici

        socketLicensePlateService.sendLicensePlateDetails(licensePlate);
        System.out.println("Nouvelle plaque créée - ID: " + licensePlate.getId() +
                ", Type: " + licensePlate.getRegistration_number() + "Device: " + licensePlate.getDevice() );
        //sendValidationToClients(event);
    }

}