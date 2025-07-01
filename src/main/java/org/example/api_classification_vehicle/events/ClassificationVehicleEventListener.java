package org.example.api_classification_vehicle.events;

import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
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
public class ClassificationVehicleEventListener {
    private final VehicleClassificationService vehicleClassificationService;

    private final WebSocketService webSocketService;

    @Autowired
    public ClassificationVehicleEventListener(VehicleClassificationService vehicleClassificationService, WebSocketService webSocketService) {
        this.vehicleClassificationService = vehicleClassificationService;
        this.webSocketService = webSocketService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleValidationCreatedEvent(ClassificationVehicleCreatedEvent event) {
        //Validation validation = validationsService.findWithAssociationsById(event.getValidationId())
        VehicleClassification classification = (VehicleClassification) event.getSource();
        // Traitez l'événement ici
        webSocketService.sendClassificationDetails(classification);
        System.out.println("Nouvelle classification créée - ID: " + classification.getId() +
                ", Type: " + classification.getVehicleType() +
                ", Classe: " + classification.getVehicleClass());
        //sendValidationToClients(event);
    }

   // private  sendValidationToClients(event) {

     //   return event;
        // Ensure you're not accidentally calling toString() on lazy entities
       /// validationsService.sendValidationToClientsTime(validation);
    //}
}