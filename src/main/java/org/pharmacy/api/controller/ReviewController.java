package org.pharmacy.api.controller;

import org.pharmacy.api.dto.ApiResponse;
import org.pharmacy.api.dto.ReviewRequest;
import org.pharmacy.api.model.Review;
import org.pharmacy.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        Review review = reviewService.createReview(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", review));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        Double averageRating = reviewService.getAverageRating(productId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("averageRating", averageRating);
        response.put("totalReviews", reviews.size());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
