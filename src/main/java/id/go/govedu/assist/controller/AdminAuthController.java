package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.admin.AdminLoginRequest;
import id.go.govedu.assist.dto.admin.AdminLoginResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Admin authenticated successfully", response));
    }
}
