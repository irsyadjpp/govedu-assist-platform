package id.go.govedu.assist.dto.delegation;

import java.time.LocalDateTime;
import java.util.UUID;

public record RevokeDelegationResponse(
    UUID delegation_id,
    boolean is_active,
    LocalDateTime revoked_at
) {}
