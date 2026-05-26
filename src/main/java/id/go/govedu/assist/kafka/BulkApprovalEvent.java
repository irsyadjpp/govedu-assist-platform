package id.go.govedu.assist.kafka;

import java.util.List;
import java.util.UUID;

public record BulkApprovalEvent(
    UUID batchId,
    List<UUID> applicationIds,
    UUID adminId,
    String notes
) {}
