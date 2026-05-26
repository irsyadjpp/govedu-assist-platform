package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.model.AuditLog;
import id.go.govedu.assist.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('TIER_3_PPK')")
    public ApiResponse<List<AuditLog>> getAuditHistory(
            @RequestParam String entityName,
            @RequestParam UUID entityId) {
        List<AuditLog> logs = auditLogRepository.findByEntityNameAndEntityIdOrderByCreatedAtDesc(entityName, entityId);
        return ApiResponse.success(logs);
    }
}
