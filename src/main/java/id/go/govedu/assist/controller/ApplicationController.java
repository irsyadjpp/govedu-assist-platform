package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.application.ApplicationResponse;
import id.go.govedu.assist.dto.application.CreateApplicationRequest;
import id.go.govedu.assist.dto.application.SubmitResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CreateApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(userId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Application draft created", response));
    }

    @PostMapping("/{application_id}/submit")
    public ResponseEntity<ApiResponse<SubmitResponse>> submitApplication(
            @PathVariable("application_id") UUID applicationId) {
        SubmitResponse response = applicationService.submitApplication(applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully. It is now waiting for review.", response));
    }
}
