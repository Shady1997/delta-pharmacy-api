package org.pharmacy.api.controller;

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
public class ChatController {

    private final SupportService supportService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String response = supportService.mockChatWithPharmacist(message);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
