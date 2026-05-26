package id.go.govedu.assist.dto.application;

import java.time.LocalDateTime;
import java.util.UUID;

public record DecisionResponse(
    UUID application_id,
    String status,
    UUID decision_by,
    String notes,
    LocalDateTime updated_at
) {}
