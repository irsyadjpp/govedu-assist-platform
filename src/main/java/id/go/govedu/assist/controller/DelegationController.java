package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.dto.delegation.CreateDelegationRequest;
import id.go.govedu.assist.dto.delegation.DelegationResponse;
import id.go.govedu.assist.dto.delegation.RevokeDelegationResponse;
import id.go.govedu.assist.security.JwtUserDetails;
import id.go.govedu.assist.service.DelegationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/delegations")
@RequiredArgsConstructor
public class DelegationController {

    private final DelegationService delegationService;

    @PostMapping
    public ResponseEntity<ApiResponse<DelegationResponse>> createDelegation(
            @AuthenticationPrincipal JwtUserDetails adminDetails,
            @Valid @RequestBody CreateDelegationRequest request) {
        DelegationResponse response = delegationService.createDelegation(adminDetails.getUserId(), request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Delegation created successfully", response));
    }

    @PatchMapping("/{delegation_id}/revoke")
    public ResponseEntity<ApiResponse<RevokeDelegationResponse>> revokeDelegation(
            @PathVariable("delegation_id") UUID delegationId,
            @AuthenticationPrincipal JwtUserDetails adminDetails) {
        RevokeDelegationResponse response = delegationService.revokeDelegation(delegationId, adminDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Delegation revoked successfully", response));
    }
}
