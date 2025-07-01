package org.example.api_classification_vehicle.controleur;

import org.example.api_classification_vehicle.service.LicensePlateReportService;
import org.example.api_classification_vehicle.service.LicensePlateService;
import org.example.api_classification_vehicle.service.VehicleClassificationReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports/license-plate")
public class LicensePlateReportController {

    private final LicensePlateReportService reportService;

    @Autowired
    public LicensePlateReportController(LicensePlateReportService reportService) {
        this.reportService = reportService;
    }



    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportToCsv(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) int axleCount,
            @RequestParam(required = false) int tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String csv = reportService.exportToCsv(vehicleType, device,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vehicle_classifications_" + startDate + "_to_" + endDate + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) int axleCount,
            @RequestParam(required = false) int tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        byte[] pdfBytes = reportService.exportToPdf(vehicleType, device,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicle_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportToJson(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) int axleCount,
            @RequestParam(required = false) int tarrif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String json = reportService.exportToJson(vehicleType, device,startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vehicle_classifications_" + startDate + "_to_" + endDate + ".json")
                .body(json);
    }
}