package id.go.govedu.assist.dto.bulk;

import java.time.LocalDateTime;
import java.util.UUID;

public record BulkApprovalStatusResponse(
    UUID batch_id,
    String batch_number,
    int total_applications,
    int processed_count,
    double progress_percentage,
    String status,
    LocalDateTime started_at
) {}
