package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.dto.fraud.ResolveFraudFlagRequest;
import id.go.govedu.assist.dto.fraud.ResolveFraudFlagResponse;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.FraudFlag;
import id.go.govedu.assist.repository.ApplicationRepository;
import id.go.govedu.assist.repository.FraudFlagRepository;
import id.go.govedu.assist.security.JwtUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/fraud-flags")
@RequiredArgsConstructor
public class FraudFlagController {

    private final FraudFlagRepository fraudFlagRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping("/{flag_id}/resolve")
    @PreAuthorize("hasRole('TIER_2_PPK') or hasRole('TIER_3_PPK')")
    public ResponseEntity<ApiResponse<ResolveFraudFlagResponse>> resolveFraudFlag(
            @PathVariable("flag_id") UUID flagId,
            @AuthenticationPrincipal JwtUserDetails adminDetails,
            @Valid @RequestBody ResolveFraudFlagRequest request) {

        FraudFlag flag = fraudFlagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("Fraud flag not found"));

        Application application = flag.getApplication();

        if ("CONFIRMED_FRAUD".equals(request.resolutionAction())) {
            flag.setStatus(FraudFlag.FlagStatus.CONFIRMED);
            flag.setResolutionNotes(request.notes());
            application.setStatus(Application.ApplicationStatus.REJECTED);
        } else if ("FALSE_ALARM".equals(request.resolutionAction())) {
            flag.setStatus(FraudFlag.FlagStatus.FALSE_ALARM);
            flag.setResolutionNotes(request.notes());
            application.setStatus(Application.ApplicationStatus.IN_REVIEW);
            application.setHasActiveFraudFlag(false);
        }

        fraudFlagRepository.save(flag);
        applicationRepository.save(application);

        ResolveFraudFlagResponse response = new ResolveFraudFlagResponse(
                flagId,
                flag.getStatus().name(),
                application.getStatus().name()
        );

        return ResponseEntity.ok(ApiResponse.success("Fraud flag resolved successfully", response));
    }
}
