package id.go.govedu.assist.dto.bulk;

import java.util.UUID;

public record BulkApprovalResponse(
    UUID batch_id,
    String batch_number,
    int total_submitted,
    String status,
    String check_status_url
) {}
