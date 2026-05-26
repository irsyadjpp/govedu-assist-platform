package id.go.govedu.assist.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportTaskStatusResponse(
    UUID taskId,
    String status,
    LocalDateTime requestedAt,
    LocalDateTime completedAt,
    String downloadUrl
) {}
