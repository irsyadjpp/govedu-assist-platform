package id.go.govedu.assist.controller;

import id.go.govedu.assist.dto.common.ApiResponse;
import id.go.govedu.assist.dto.report.ReportGenerationRequest;
import id.go.govedu.assist.dto.report.ReportGenerationResponse;
import id.go.govedu.assist.dto.report.ReportTaskStatusResponse;
import id.go.govedu.assist.kafka.ReportTaskMessage;
import id.go.govedu.assist.model.Admin;
import id.go.govedu.assist.model.ReportTask;
import id.go.govedu.assist.repository.AdminRepository;
import id.go.govedu.assist.repository.ReportTaskRepository;
import id.go.govedu.assist.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportTaskRepository reportTaskRepository;
    private final AdminRepository adminRepository;
    private final KafkaTemplate<String, ReportTaskMessage> kafkaTemplate;

    @PostMapping("/export")
    @PreAuthorize("hasRole('TIER_3_PPK')")
    public ResponseEntity<ApiResponse<ReportGenerationResponse>> requestReport(
            @AuthenticationPrincipal JwtUserDetails adminDetails,
            @RequestBody ReportGenerationRequest request) {

        Admin admin = adminRepository.findById(UUID.fromString(adminDetails.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        ReportTask task = new ReportTask(
                admin,
                ReportTask.ReportType.valueOf(request.reportType()),
                request.filters()
        );

        task = reportTaskRepository.save(task);

        ReportTaskMessage message = new ReportTaskMessage(
                task.getId(),
                request.reportType(),
                request.filters()
        );

        kafkaTemplate.send("report-generation-tasks", task.getId().toString(), message);

        ReportGenerationResponse response = new ReportGenerationResponse(
                task.getId(),
                task.getStatus().name()
        );

        return ResponseEntity.accepted().body(
                ApiResponse.success("Report generation task accepted. You will be notified when it's ready.", response)
        );
    }

    @GetMapping("/tasks/{task_id}")
    @PreAuthorize("hasRole('TIER_3_PPK')")
    public ResponseEntity<ApiResponse<ReportTaskStatusResponse>> getTaskStatus(
            @PathVariable("task_id") UUID taskId) {

        ReportTask task = reportTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Report task not found"));

        ReportTaskStatusResponse response = new ReportTaskStatusResponse(
                task.getId(),
                task.getStatus().name(),
                task.getRequestedAt(),
                task.getCompletedAt(),
                task.getFileUrl()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
