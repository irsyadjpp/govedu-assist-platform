package id.go.govedu.assist.dto.fraud;

import java.util.UUID;

public record ResolveFraudFlagResponse(
    UUID flagId,
    String status,
    String applicationStatusUpdatedTo
) {}
