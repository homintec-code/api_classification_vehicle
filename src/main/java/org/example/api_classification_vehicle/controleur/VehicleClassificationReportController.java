package org.example.api_classification_vehicle.controleur;

import org.example.api_classification_vehicle.service.VehicleClassificationReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/vehicle-classifications")
public class VehicleClassificationReportController {

    private final VehicleClassificationReportService reportService;

    @Autowired
    public VehicleClassificationReportController(VehicleClassificationReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/by-vehicle-type")
    public Map<String, Long> getVehicleTypeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return reportService.generateVehicleTypeReport(startDate, endDate);
    }

    @GetMapping("/by-vehicle-class")
    public Map<String, Long> getVehicleClassReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return reportService.generateVehicleClassReport(startDate, endDate);
    }

    @GetMapping("/by-axle-count")
    public Map<Integer, Long> getAxleCountReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return reportService.generateAxleCountReport(startDate, endDate);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportToCsv(
            @RequestParam(required = false,defaultValue = "") String vehicleType,
            @RequestParam(required = false,defaultValue = "") String device,
            @RequestParam(required = false,defaultValue = "0") Integer axleCount,
            @RequestParam(required = false,defaultValue = "0") Integer tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String csv = reportService.exportToCsv(vehicleType, device,axleCount,tarrif,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vehicle_classifications_" + startDate + "_to_" + endDate + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false,defaultValue = "") String vehicleType,
            @RequestParam(required = false,defaultValue = "") String device,
            @RequestParam(required = false,defaultValue = "0") Integer axleCount,
            @RequestParam(required = false,defaultValue = "0") Integer tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        byte[] pdfBytes = reportService.exportToPdf(vehicleType, device,axleCount,tarrif,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicle_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportToJson(
            @RequestParam(required = false,defaultValue = "") String vehicleType,
            @RequestParam(required = false,defaultValue = "") String device,
            @RequestParam(required = false,defaultValue = "0") Integer axleCount,
            @RequestParam(required = false,defaultValue = "0") Integer tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String json = reportService.exportToJson(vehicleType, device,axleCount,tarrif,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vehicle_classifications_" + startDate + "_to_" + endDate + ".json")
                .body(json);
    }
}