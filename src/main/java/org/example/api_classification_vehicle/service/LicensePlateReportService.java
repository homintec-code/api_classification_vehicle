package org.example.api_classification_vehicle.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.LicensePlateRepository;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LicensePlateReportService {

    private final LicensePlateRepository licensePlateRepository;

    @Autowired
    public LicensePlateReportService(LicensePlateRepository licensePlateRepository) {
        this.licensePlateRepository = licensePlateRepository;
    }


    /**
     * Exporte les données de classification au format CSV pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return String contenant les données au format CSV
     */
    @Transactional(readOnly = true)
    public String exportToCsv(String registrationNumber,String device,LocalDateTime startDate, LocalDateTime endDate) {
        try {

            List<LicensePlate> licensePlates;
            if (registrationNumber != null || device != null || (startDate != null && endDate != null)) {
                licensePlates = licensePlateRepository
                        .findByOptionalRegistrationOptionalOrDeviceOrCreatedAtBetween(registrationNumber, device,startDate, endDate);
            } else {

                licensePlates = licensePlateRepository
                        .findByCreatedAtBetween(startDate, endDate);
            }



            StringBuilder csvBuilder = new StringBuilder();
            // En-tête CSV
            csvBuilder.append("immatriculation,Voie,Date de création\n");

            // Données
            licensePlates.forEach(vc -> {
                csvBuilder.append("\"")
                        .append(escapeCsv(vc.getRegistration_number())).append("\",\"")
                        .append(escapeCsv(vc.getDevice())).append("\",")
                        .append(vc.getCreatedAt()).append("\"\n");
            });

            return csvBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV", e);
        }
    }

    // Méthode pour échapper les caractères spéciaux CSV

    private String escapeCsv(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\"", "\"\"");
    }
    /**
     * Exporte les données de classification au format JSON pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return String contenant les données au format JSON
     */
    @Transactional(readOnly = true)
    public String exportToJson(String registrationNumber,String device,LocalDateTime startDate, LocalDateTime endDate) {


        // 1. Récupérer les données
        List<LicensePlate> licensePlates;
        if (registrationNumber != null || device != null || (startDate != null && endDate != null)) {
            licensePlates = licensePlateRepository
                    .findByOptionalRegistrationOptionalOrDeviceOrCreatedAtBetween(registrationNumber, device, startDate, endDate);
        } else {
            licensePlates = licensePlateRepository.findByCreatedAtBetween(startDate, endDate);
        }



        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n");

        licensePlates.forEach(vc -> {
            jsonBuilder.append("  {\n")
                    .append("    \"immatriculation\": \"").append(vc.getRegistration_number()).append("\",\n")
                    .append("    \"Image PLaque\": \"").append(vc.getImagePlate64()).append("\",\n")
                    .append("    \"Image vehicule\": ").append(vc.getImageVehicleBase64()).append(",\n")
                    .append("    \"device\": \"").append(vc.getDevice()).append("\",\n")
                    .append("    \"createdAt\": \"").append(vc.getCreatedAt()).append("\"\n")
                    .append("  },\n");
        });

        // Supprimer la dernière virgule si nécessaire
        if (!licensePlates.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 2);
        }

        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }



    @Transactional(readOnly = true)
    public byte[] exportToPdf(String registrationNumber, String device, LocalDateTime startDate, LocalDateTime endDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 1. Récupérer les données
            List<LicensePlate> licensePlates;
            if (registrationNumber != null || device != null || (startDate != null && endDate != null)) {
                licensePlates = licensePlateRepository
                        .findByOptionalRegistrationOptionalOrDeviceOrCreatedAtBetween(registrationNumber, device, startDate, endDate);
            } else {
                licensePlates = licensePlateRepository.findByCreatedAtBetween(startDate, endDate);
            }

            // 2. Créer le document PDF
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 3. Ajouter les images d’en-tête (si disponibles)
            try {
                if (!licensePlates.isEmpty()) {
                    LicensePlate lp = licensePlates.get(0);

                    if (lp.getImagePlate64() != null) {
                        byte[] imageBytesPlaque = Base64.getDecoder().decode(lp.getImagePlate64());
                        Image logo = Image.getInstance(imageBytesPlaque);
                        logo.scaleToFit(100, 100);
                        logo.setAbsolutePosition(document.right() - logo.getScaledWidth() - 36, document.top() - logo.getScaledHeight() - 36);
                        document.add(logo);
                    }

                    if (lp.getImageVehicleBase64() != null) {
                        byte[] imageBytesVehicle = Base64.getDecoder().decode(lp.getImageVehicleBase64());
                        Image vehicleImage = Image.getInstance(imageBytesVehicle);
                        vehicleImage.scaleToFit(150, 150);
                        vehicleImage.setAbsolutePosition(36, document.top() - vehicleImage.getScaledHeight() - 36);
                        document.add(vehicleImage);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ajout des images : " + e.getMessage());
            }

            // 4. Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des immatriculations des Véhicules", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // 5. Période
            if (startDate != null && endDate != null) {
                Font metadataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Paragraph period = new Paragraph(
                        String.format("Période : %s au %s",
                                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                        metadataFont);
                period.setSpacingAfter(15f);
                document.add(period);
            }

            // 6. Créer tableau
            PdfPTable table = new PdfPTable(6); // 6 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // En-têtes
            Stream.of("Immatriculation", "Classe véhicule", "Caméra", "Date de création", "Image Plaque", "Image Véhicule")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    });

            // Contenu
            for (LicensePlate lp : licensePlates) {
                table.addCell(lp.getRegistration_number() != null ? lp.getRegistration_number() : "N/A");
                table.addCell(lp.getDevice() != null ? lp.getDevice() : "N/A");
                table.addCell(lp.getCreatedAt() != null
                        ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(lp.getCreatedAt())
                        : "N/A");

                // Image plaque
                if (lp.getImagePlate64() != null) {
                    try {
                        byte[] imgBytes = Base64.getDecoder().decode(lp.getImagePlate64());
                        Image img = Image.getInstance(imgBytes);
                        img.scaleToFit(50, 50);
                        PdfPCell imageCell = new PdfPCell(img, true);
                        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        imageCell.setPadding(5);
                        table.addCell(imageCell);
                    } catch (Exception e) {
                        table.addCell("Pas d'image");
                    }
                } else {
                    table.addCell("N/A");
                }

                // Image véhicule
                if (lp.getImageVehicleBase64() != null) {
                    try {
                        byte[] imgBytes = Base64.getDecoder().decode(lp.getImageVehicleBase64());
                        Image img = Image.getInstance(imgBytes);
                        img.scaleToFit(50, 50);
                        PdfPCell imageCell = new PdfPCell(img, true);
                        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        imageCell.setPadding(5);
                        table.addCell(imageCell);
                    } catch (Exception e) {
                        table.addCell("Pas d'image");
                    }
                } else {
                    table.addCell("N/A");
                }
            }

            document.add(table);

            // 7. Pied de page
            Paragraph footer = new Paragraph(
                    String.format("Généré le %s - Total : %d enregistrements",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            licensePlates.size()),
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)
            );
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20f);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

}