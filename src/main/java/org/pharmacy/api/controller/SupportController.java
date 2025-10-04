package org.pharmacy.api.controller;

import org.pharmacy.api.dto.ApiResponse;
import org.pharmacy.api.dto.SupportTicketRequest;
import org.pharmacy.api.model.SupportTicket;
import org.pharmacy.api.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/ticket")
    public ResponseEntity<ApiResponse<SupportTicket>> createTicket(
            @Valid @RequestBody SupportTicketRequest request,
            Authentication authentication) {
        SupportTicket ticket = supportService.createTicket(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", ticket));
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<ApiResponse<SupportTicket>> getTicket(@PathVariable Long id) {
        SupportTicket ticket = supportService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getUserTickets(Authentication authentication) {
        List<SupportTicket> tickets = supportService.getUserTickets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/tickets/all")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getAllTickets() {
        List<SupportTicket> tickets = supportService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @PutMapping("/ticket/{id}/status")
    public ResponseEntity<ApiResponse<SupportTicket>> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        SupportTicket ticket = supportService.updateTicketStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Ticket status updated", ticket));
    }

    @PostMapping("/ticket/{id}/response")
    public ResponseEntity<ApiResponse<SupportTicket>> addResponse(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String response = body.get("response");
        SupportTicket ticket = supportService.addResponse(id, response);
        return ResponseEntity.ok(ApiResponse.success("Response added", ticket));
    }
}
