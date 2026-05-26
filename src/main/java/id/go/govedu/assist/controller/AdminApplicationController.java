package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.application.DecisionRequest;
import id.go.govedu.assist.dto.application.DecisionResponse;
import id.go.govedu.assist.dto.application.ReviewResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.security.JwtUserDetails;
import id.go.govedu.assist.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/applications")
@RequiredArgsConstructor
public class AdminApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{application_id}/review")
    @PreAuthorize("hasAnyRole('TIER_1_UNIV', 'TIER_2_GOV')")
    public ResponseEntity<ApiResponse<ReviewResponse>> claimForReview(
            @PathVariable("application_id") UUID applicationId,
            @AuthenticationPrincipal JwtUserDetails adminDetails) {
        ReviewResponse response = applicationService.claimForReview(applicationId, adminDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Application is now under your review", response));
    }

    @PostMapping("/{application_id}/decisions")
    @PreAuthorize("hasAnyRole('TIER_1_UNIV', 'TIER_2_GOV')")
    public ResponseEntity<ApiResponse<DecisionResponse>> makeDecision(
            @PathVariable("application_id") UUID applicationId,
            @AuthenticationPrincipal JwtUserDetails adminDetails,
            @Valid @RequestBody DecisionRequest request) {
        DecisionResponse response = applicationService.makeDecision(applicationId, adminDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Decision applied successfully", response));
    }
}
