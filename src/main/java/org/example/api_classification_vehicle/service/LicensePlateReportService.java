package org.example.api_classification_vehicle.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.criteria.Predicate;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.api_classification_vehicle.model.LicensePlate;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.LicensePlateRepository;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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



            Specification<LicensePlate> spec = findWithFilters(registrationNumber, device, startDate, endDate);
            List<LicensePlate> licensePlates = licensePlateRepository.findAll(spec);
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



        Specification<LicensePlate> spec = findWithFilters(registrationNumber, device, startDate, endDate);
        List<LicensePlate> licensePlates = licensePlateRepository.findAll(spec);


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



            Specification<LicensePlate> spec = findWithFilters(registrationNumber, device, startDate, endDate);
            List<LicensePlate> licensePlates = licensePlateRepository.findAll(spec);
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des plaques immatriculations de Véhicules", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Période
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

            // Tableau
            PdfPTable table = new PdfPTable(4); // 7 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // En-têtes
            Stream.of("Immatriculation", "Voie", "Date de création", "Image Plaque")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    });

            // Contenu
            for (LicensePlate vc : licensePlates) {
                table.addCell(vc.getRegistration_number() != null ? vc.getRegistration_number() : "N/A");
                table.addCell(vc.getDevice() != null ? vc.getDevice() : "N/A");
                table.addCell(vc.getCreatedAt() != null ?
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(vc.getCreatedAt()) : "N/A");

                // Image
                if (vc.getImagePlate64() != null && !vc.getImagePlate64().isEmpty()) {
                    try {
                        byte[] imgBytes = Base64.getDecoder().decode(vc.getImagePlate64());
                        Image img = Image.getInstance(imgBytes);
                        img.scaleToFit(50, 50);
                        PdfPCell imageCell = new PdfPCell(img, true);
                        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        imageCell.setPadding(5);
                        table.addCell(imageCell);
                    } catch (Exception e) {
                        table.addCell("Erreur image");
                    }
                } else {
                    table.addCell("Pas d'image");
                }
            }

            document.add(table);

            // Pied de page
            Paragraph footer = new Paragraph(
                    String.format("Généré le %s - Total: %d enregistrements",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            licensePlates.size()),
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


    public static Specification<LicensePlate> findWithFilters(
            String registrationNumber, String device,
            LocalDateTime startDate, LocalDateTime endDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (registrationNumber != null && !registrationNumber.isEmpty()) {
                predicates.add(cb.equal(root.get("registration_number"), registrationNumber));
            }

            if (device != null && !device.isEmpty()) {
                predicates.add(cb.equal(root.get("device"), device));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}