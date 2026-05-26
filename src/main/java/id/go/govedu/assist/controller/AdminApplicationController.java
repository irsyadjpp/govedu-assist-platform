package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.application.DecisionRequest;
import id.go.govedu.assist.dto.application.DecisionResponse;
import id.go.govedu.assist.dto.application.ReviewResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/applications")
@RequiredArgsConstructor
public class AdminApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{application_id}/review")
    public ResponseEntity<ApiResponse<ReviewResponse>> claimForReview(
            @PathVariable("application_id") UUID applicationId,
            @AuthenticationPrincipal UUID adminId) {
        ReviewResponse response = applicationService.claimForReview(applicationId, adminId);
        return ResponseEntity.ok(ApiResponse.success("Application is now under your review", response));
    }

    @PostMapping("/{application_id}/decisions")
    public ResponseEntity<ApiResponse<DecisionResponse>> makeDecision(
            @PathVariable("application_id") UUID applicationId,
            @AuthenticationPrincipal UUID adminId,
            @Valid @RequestBody DecisionRequest request) {
        DecisionResponse response = applicationService.makeDecision(applicationId, adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Decision applied successfully", response));
    }
}
