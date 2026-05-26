package id.go.govedu.assist.controller;

import id.go.govedu.assist.annotation.Idempotent;
import id.go.govedu.assist.dto.bulk.BulkApprovalRequest;
import id.go.govedu.assist.dto.bulk.BulkApprovalResponse;
import id.go.govedu.assist.dto.bulk.BulkApprovalStatusResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.security.JwtUserDetails;
import id.go.govedu.assist.service.BulkApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/applications/bulk-approve")
@RequiredArgsConstructor
public class BulkApprovalController {

    private final BulkApprovalService bulkApprovalService;

    @PostMapping
    @PreAuthorize("hasRole('TIER_3_PPK')")
    @Idempotent
    public ResponseEntity<ApiResponse<BulkApprovalResponse>> initiateBulkApproval(
            @AuthenticationPrincipal JwtUserDetails adminDetails,
            @Valid @RequestBody BulkApprovalRequest request) {
        BulkApprovalResponse response = bulkApprovalService.initiateBulkApproval(adminDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Bulk approval accepted and is processing in the background.", response));
    }

    @GetMapping("/{batch_id}/status")
    @PreAuthorize("hasRole('TIER_3_PPK')")
    public ResponseEntity<ApiResponse<BulkApprovalStatusResponse>> getBulkApprovalStatus(
            @PathVariable("batch_id") UUID batchId) {
        BulkApprovalStatusResponse response = bulkApprovalService.getBulkApprovalStatus(batchId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
