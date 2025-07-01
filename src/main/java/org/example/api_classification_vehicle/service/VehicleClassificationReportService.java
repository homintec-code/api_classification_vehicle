package org.example.api_classification_vehicle.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VehicleClassificationReportService {

    private final VehicleClassificationRepository vehicleClassificationRepository;

    @Autowired
    public VehicleClassificationReportService(VehicleClassificationRepository vehicleClassificationRepository) {
        this.vehicleClassificationRepository = vehicleClassificationRepository;
    }

    /**
     * Génère un rapport des classifications par type de véhicule pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Map avec le type de véhicule comme clé et le nombre d'occurrences comme valeur
     */
    public Map<String, Long> generateVehicleTypeReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<VehicleClassification> classifications = vehicleClassificationRepository
                .findByCreatedAtBetween(startDate, endDate);

        return classifications.stream()
                .collect(Collectors.groupingBy(
                        VehicleClassification::getVehicleType,
                        Collectors.counting()
                ));
    }

    /**
     * Génère un rapport des classifications par classe de véhicule pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Map avec la classe de véhicule comme clé et le nombre d'occurrences comme valeur
     */
    public Map<String, Long> generateVehicleClassReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<VehicleClassification> classifications = vehicleClassificationRepository
                .findByCreatedAtBetween(startDate, endDate);

        return classifications.stream()
                .collect(Collectors.groupingBy(
                        VehicleClassification::getVehicleClass,
                        Collectors.counting()
                ));
    }

    /**
     * Génère un rapport des classifications par nombre d'essieux pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Map avec le nombre d'essieux comme clé et le nombre d'occurrences comme valeur
     */
    public Map<Integer, Long> generateAxleCountReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<VehicleClassification> classifications = vehicleClassificationRepository
                .findByCreatedAtBetween(startDate, endDate);

        return classifications.stream()
                .collect(Collectors.groupingBy(
                        VehicleClassification::getAxleCount,
                        Collectors.counting()
                ));
    }

    /**
     * Récupère toutes les classifications pour une période donnée (paginated)
     * @param startDate Date de début
     * @param endDate Date de fin
     * @param pageable Paramètres de pagination
     * @return Page de VehicleClassification
     */
    public Page<VehicleClassification> getClassificationsByPeriod(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return vehicleClassificationRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }

    /**
     * Exporte les données de classification au format CSV pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return String contenant les données au format CSV
     */
    @Transactional(readOnly = true)
    public String exportToCsv(String vehicleType,String device,int axleCount, int tarrif,LocalDateTime startDate, LocalDateTime endDate) {
        try {

            List<VehicleClassification> classifications ;
            if (vehicleType != null || device != null || (startDate != null && endDate != null)) {
                classifications = vehicleClassificationRepository
                        .findByOptionalVehicle(vehicleType, device,axleCount,tarrif,startDate, endDate);
            } else {

                 classifications = vehicleClassificationRepository
                        .findByCreatedAtBetween(startDate, endDate);
            }



            StringBuilder csvBuilder = new StringBuilder();
            // En-tête CSV
            csvBuilder.append("Type,Classe,Nombre d'essieux,Tarif,Appareil,Date de création\n");

            // Données
            classifications.forEach(vc -> {
                csvBuilder.append("\"")
                        .append(escapeCsv(vc.getVehicleType())).append("\",\"")
                        .append(escapeCsv(vc.getVehicleClass())).append("\",")
                        .append(vc.getAxleCount()).append(",")
                        .append(vc.getTarrif()).append(",\"")
                        .append(escapeCsv(vc.getDevice())).append("\",\"")
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
    public String exportToJson(String vehicleType,String device, int axleCount, int tarrif,LocalDateTime startDate, LocalDateTime endDate) {
        List<VehicleClassification> classifications ;
        if (vehicleType != null || device != null || (startDate != null && endDate != null)) {
            classifications = vehicleClassificationRepository
                    .findByOptionalVehicle(vehicleType, device,axleCount,tarrif,startDate, endDate);
        } else {

            classifications = vehicleClassificationRepository
                    .findByCreatedAtBetween(startDate, endDate);
        }


        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n");

        classifications.forEach(vc -> {
            jsonBuilder.append("  {\n")
                    .append("    \"type\": \"").append(vc.getVehicleType()).append("\",\n")
                    .append("    \"class\": \"").append(vc.getVehicleClass()).append("\",\n")
                    .append("    \"axleCount\": ").append(vc.getAxleCount()).append(",\n")
                    .append("    \"tarrif\": ").append(vc.getTarrif()).append(",\n")
                    .append("    \"device\": \"").append(vc.getDevice()).append("\",\n")
                    .append("    \"imageBase64\": \"").append(vc.getImageBase64()).append("\",\n")
                    .append("    \"createdAt\": \"").append(vc.getCreatedAt()).append("\"\n")
                    .append("  },\n");
        });

        // Supprimer la dernière virgule si nécessaire
        if (!classifications.isEmpty()) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 2);
        }

        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }

    /**
     * Exporte les données de classification au format PDF pour une période donnée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return byte[] contenant le PDF généré
     *//*
    @Transactional(readOnly = true)
    public byte[] exportToPdf(LocalDateTime startDate, LocalDateTime endDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1. Récupérer les données
            List<VehicleClassification> classifications = vehicleClassificationRepository
                    .findByCreatedAtBetween(startDate, endDate);

            // 2. Créer le document PDF
            Document document = new Document(PageSize.A4.rotate()); // Paysage pour plus d'espace
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 3. Ajouter un titre
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des Classifications de Véhicules", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // 4. Ajouter les métadonnées de la période
            Font metadataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph period = new Paragraph(
                    String.format("Période : %s au %s",
                            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                    metadataFont);
            period.setSpacingAfter(15f);
            document.add(period);

            // 5. Créer un tableau pour les données
            PdfPTable table = new PdfPTable(6); // 6 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // En-têtes du tableau
            Stream.of("Type", "Classe", "Nombre d'essieux", "Tarif", "Appareil", "Date de création")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    });

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            // Remplir le tableau avec les données
            classifications.forEach(vc -> {
                table.addCell(vc.getVehicleType());
                table.addCell(vc.getVehicleClass());
                table.addCell(String.valueOf(vc.getAxleCount()));
                table.addCell(String.valueOf(vc.getTarrif()));
                table.addCell(vc.getDevice());
                table.addCell(sdf.format(vc.getCreatedAt()));
            });

            document.add(table);

            // 6. Ajouter un pied de page
            Paragraph footer = new Paragraph(
                    String.format("Généré le %s - Total: %d enregistrements",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            classifications.size()),
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20f);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }*/


    @Transactional(readOnly = true)
    public byte[] exportToPdf(String vehicleType,String device,int axleCount, int tarrif,LocalDateTime startDate, LocalDateTime endDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1. Récupérer les données



            List<VehicleClassification> classifications ;
            if (vehicleType != null || device != null || (startDate != null && endDate != null)) {
                classifications = vehicleClassificationRepository
                        .findByOptionalVehicle(vehicleType, device,axleCount,tarrif, startDate, endDate);
            } else {

                classifications = vehicleClassificationRepository
                        .findByCreatedAtBetween(startDate, endDate);
            }


            // 2. Créer le document PDF
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 3. Ajouter le logo (si disponible)
            try {
                if (classifications != null && !classifications.isEmpty()
                        && classifications.get(0).getImageBase64() != null) {
                    String base64Image = classifications.get(0).getImageBase64();
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    Image logo = Image.getInstance(imageBytes);

                    // Redimensionner le logo
                    logo.scaleToFit(100, 100); // Ajustez selon vos besoins

                    // Positionner le logo
                    logo.setAbsolutePosition(
                            document.right() - logo.getScaledWidth() - 36,  // 36 = marge de 0.5 pouce
                            document.top() - logo.getScaledHeight() - 36
                    );
                    document.add(logo);
                }
            } catch (Exception e) {
                // Ne pas bloquer la génération du PDF si l'image pose problème
                System.err.println("Erreur lors de l'ajout du logo: " + e.getMessage());
            }

            // 3. Ajouter un titre
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des Classifications de Véhicules", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // 4. Ajouter les métadonnées de la période
            Font metadataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph period = new Paragraph(
                    String.format("Période : %s au %s",
                            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                    metadataFont);
            period.setSpacingAfter(15f);
            document.add(period);

            // 5. Créer un tableau pour les données
            PdfPTable table = new PdfPTable(7); // 6 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // En-têtes du tableau
            Stream.of("Type", "Classe", "Nombre d'essieux", "Tarif", "Appareil", "Date de création","Images")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    });
            // ... (le reste de votre code existant: titre, période, tableau...)

            // Dans la boucle d'ajout des données au tableau:
            classifications.forEach(vc -> {
                table.addCell(vc.getVehicleType());
                table.addCell(vc.getVehicleClass());
                table.addCell(String.valueOf(vc.getAxleCount()));
                table.addCell(String.valueOf(vc.getTarrif()));
                table.addCell(vc.getDevice());
                table.addCell(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(vc.getCreatedAt()));

                // Option: Ajouter une miniature de l'image dans une cellule du tableau
                if (vc.getImageBase64() != null) {
                    try {
                        byte[] imgBytes = Base64.getDecoder().decode(vc.getImageBase64());
                        Image img = Image.getInstance(imgBytes);
                        img.scaleToFit(40, 40); // Taille de la miniature
                        PdfPCell imageCell = new PdfPCell(img, true);
                        imageCell.setPadding(5);
                        table.addCell(imageCell);
                    } catch (Exception e) {
                        table.addCell("Image non disponible");
                    }
                } else {
                    table.addCell("Pas d'image");
                }
            });

            document.add(table);
            // 6. Ajouter un pied de page
            Paragraph footer = new Paragraph(
                    String.format("Généré le %s - Total: %d enregistrements",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            classifications.size()),
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
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