package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.dto.ekyc.EkycVerifyRequest;
import id.go.govedu.assist.dto.ekyc.EkycVerifyResponse;
import id.go.govedu.assist.service.EkycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/ekyc")
@RequiredArgsConstructor
public class EkycController {

    private final EkycService ekycService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<EkycVerifyResponse>> verifyIdentity(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody EkycVerifyRequest request) {
        EkycVerifyResponse response = ekycService.verifyIdentity(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Identity verified successfully", response));
    }
}
