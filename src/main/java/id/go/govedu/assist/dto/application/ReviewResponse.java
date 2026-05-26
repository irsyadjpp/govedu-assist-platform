package id.go.govedu.assist.dto.application;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
    UUID application_id,
    String status,
    UUID reviewer_admin_id,
    LocalDateTime updated_at
) {}
