package id.go.govedu.assist.dto.report;

import java.util.UUID;

public record ReportGenerationResponse(
    UUID taskId,
    String status
) {}
