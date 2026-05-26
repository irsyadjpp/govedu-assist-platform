package id.go.govedu.assist.dto.application;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmitResponse(
    UUID application_id,
    String status,
    LocalDateTime updated_at
) {}
