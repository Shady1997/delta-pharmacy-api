package org.pharmacy.api.controller;

import org.pharmacy.api.dto.ApiResponse;
import org.pharmacy.api.model.Prescription;
import org.pharmacy.api.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Prescription>> uploadPrescription(
            @RequestParam Long userId,
            @RequestParam String fileName,
            @RequestParam String fileType,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String notes) {

        Prescription prescription = prescriptionService.uploadPrescription(
                userId, fileName, fileType, doctorName, notes);
        return ResponseEntity.ok(ApiResponse.success("Prescription uploaded successfully", prescription));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Prescription>>> getUserPrescriptions(@PathVariable Long userId) {
        List<Prescription> prescriptions = prescriptionService.getUserPrescriptions(userId);
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Prescription>> approvePrescription(
            @PathVariable Long id,
            Authentication authentication) {
        Prescription prescription = prescriptionService.approvePrescription(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Prescription approved", prescription));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Prescription>> rejectPrescription(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String reason = body.getOrDefault("reason", "No reason provided");
        Prescription prescription = prescriptionService.rejectPrescription(id, authentication.getName(), reason);
        return ResponseEntity.ok(ApiResponse.success("Prescription rejected", prescription));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPendingPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getPendingPrescriptions();
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }
}

