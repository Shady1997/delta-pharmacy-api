/*
 * Author: Shady Ahmed
 * Date: 2025-09-27
 * Project: Delta Pharmacy API
 * My Linked-in: https://www.linkedin.com/in/shady-ahmed97/.
 */
package org.pharmacy.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pharmacy.api.dto.ApiResponse;
import org.pharmacy.api.service.SupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "8. Support", description = "Customer support ticket management")
public class ChatController {

    private final SupportService supportService;

    @PostMapping
    @Operation(summary = "Chat with pharmacist", description = "Send a message to chat with pharmacist")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String response = supportService.mockChatWithPharmacist(message);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
