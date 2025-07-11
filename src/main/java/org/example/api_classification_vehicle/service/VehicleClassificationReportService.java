package org.example.api_classification_vehicle.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.criteria.Predicate;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.api_classification_vehicle.Audit;
import org.example.api_classification_vehicle.model.VehicleClassification;
import org.example.api_classification_vehicle.repository.VehicleClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public String exportToCsv(String vehicleType,String device,Integer axleCount, Integer tarrif,LocalDateTime startDate, LocalDateTime endDate) {
        try {


            List<VehicleClassification> classifications  = listSearch(vehicleType,device,axleCount,tarrif,startDate,endDate);

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




    private List<VehicleClassification> listSearch(String vehicleType, String device, Integer axleCount, Integer tarrif,
                                                   LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must be provided.");
        }

        List<VehicleClassification> classifications;

        if (vehicleType != null && device != null && vehicleType.length() > 0 && device.length() > 0) {
            System.out.print("vehicleType : " + vehicleType +"device :" + device +"axleCount :" + axleCount);

            classifications = vehicleClassificationRepository
                    .findByOptionalVehicleAndVehicleType(vehicleType, device, startDate, endDate);
        } else if (axleCount != null && axleCount != 0 && tarrif != null && tarrif != 0) {

            System.out.print("startDate" + startDate + "endDate" + endDate  + "device :" + device +"axleCount :" + axleCount);

            classifications = vehicleClassificationRepository
                    .findByOptionalVehicleAndAxleCountAndTarrif(axleCount, tarrif, startDate, endDate);
        } else if (axleCount != null && axleCount != 0) {
            System.out.print("devicehhhhhhhhhh :" + device +"axleCount :" + axleCount);

            classifications = vehicleClassificationRepository
                    .findByOptionalVehicleAndAxleCount(axleCount, startDate, endDate);
        }
        else if (tarrif != null && tarrif != 0 && vehicleType != null && axleCount != null && axleCount != 0 && !vehicleType.isEmpty() && device != null && !device.isEmpty()) {
            System.out.println("startDate: " + startDate + " endDate: " + endDate + " device: " + device + " tarrif: " + tarrif);

            // Assuming you are using Specification for filtering:
            Specification<VehicleClassification> spec = byFilters(vehicleType, device, axleCount, tarrif, startDate, endDate);
            classifications = vehicleClassificationRepository.findAll(spec);
        }

        else if (tarrif != null && tarrif != 0) {
            System.out.print("startDate" + startDate + "endDate" + endDate  + "device :" + device +"tarrif :" + tarrif);

            classifications = vehicleClassificationRepository
                    .findByOptionalVehicleAndTarrif(tarrif, startDate, endDate);
        } else {

            System.out.print("starxxxxxxxtDate" + startDate + "endDate" + endDate  + "device :" + device +"tarrif :" + tarrif);

            classifications = vehicleClassificationRepository
                    .findByCreatedAtBetween(startDate, endDate);
        }

        return classifications;
    }

    public Specification<VehicleClassification> byFilters(String vehicleType, String device, Integer axleCount, Integer tarrif, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (vehicleType != null) {
                predicates.add(criteriaBuilder.equal(root.get("vehicleType"), vehicleType));
            }
            if (device != null) {
                predicates.add(criteriaBuilder.equal(root.get("device"), device));
            }
            if (axleCount != null) {
                predicates.add(criteriaBuilder.equal(root.get("axleCount"), axleCount));
            }
            if (tarrif != null) {
                predicates.add(criteriaBuilder.equal(root.get("tarrif"), tarrif));
            }
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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
    public String exportToJson(String vehicleType,String device, Integer axleCount, Integer tarrif,LocalDateTime startDate, LocalDateTime endDate) {

        List<VehicleClassification> classifications  = listSearch(vehicleType,device,axleCount,tarrif,startDate,endDate);
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




    @Transactional(readOnly = true)
    public byte[] exportToPdf(String vehicleType, String device, Integer axleCount,
                              Integer tarrif, LocalDateTime startDate, LocalDateTime endDate) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            List<VehicleClassification> classifications = listSearch(vehicleType, device, axleCount, tarrif, startDate, endDate);

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Rapport des Classifications de Véhicules", titleFont);
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
            PdfPTable table = new PdfPTable(7); // 7 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // En-têtes
            Stream.of("Type", "Classe", "Nombre d'essieux", "Tarif", "Appareil", "Date de création", "Images")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    });

            // Contenu
            for (VehicleClassification vc : classifications) {
                table.addCell(vc.getVehicleType() != null ? vc.getVehicleType() : "N/A");
                table.addCell(vc.getVehicleClass() != null ? vc.getVehicleClass() : "N/A");
                table.addCell(vc.getAxleCount() != 0 ? String.valueOf(vc.getAxleCount()) : "N/A");
                table.addCell(vc.getTarrif() != 0 ? String.valueOf(vc.getTarrif()) : "N/A");
                table.addCell(vc.getDevice() != null ? vc.getDevice() : "N/A");
                table.addCell(vc.getCreatedAt() != null ?
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(vc.getCreatedAt()) : "N/A");

                // Image
                if (vc.getImageBase64() != null && !vc.getImageBase64().isEmpty()) {
                    try {
                        byte[] imgBytes = Base64.getDecoder().decode(vc.getImageBase64());
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


    public List<VehicleClassification> findWithFilters(
            String vehicleType, String device, Integer axleCount,
            Integer tarrif, LocalDateTime startDate, LocalDateTime endDate) {

        return vehicleClassificationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (vehicleType != null && !vehicleType.isEmpty()) {
                predicates.add(cb.equal(root.get("vehicleClass"), vehicleType));
            }

            if (device != null && !device.isEmpty()) {
                predicates.add(cb.equal(root.get("device"), device));
            }

            if (axleCount != null) {
                predicates.add(cb.equal(root.get("axleCount"), axleCount));
            }

            if (tarrif != null) {
                predicates.add(cb.equal(root.get("tarrif"), tarrif));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            }

            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            return cb.conjunction();
        });
    }

}