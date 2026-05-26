package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.bank.BankAccountRequest;
import id.go.govedu.assist.dto.bank.BankAccountResponse;
import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.dto.user.UpdateProfileRequest;
import id.go.govedu.assist.dto.user.UserProfileResponse;
import id.go.govedu.assist.service.BankAccountService;
import id.go.govedu.assist.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(
            @AuthenticationPrincipal UUID userId) {
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PostMapping("/me/bank-accounts")
    public ResponseEntity<ApiResponse<BankAccountResponse>> registerBankAccount(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody BankAccountRequest request) {
        BankAccountResponse bankAccount = bankAccountService.registerBankAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank account registered successfully", bankAccount));
    }
}
