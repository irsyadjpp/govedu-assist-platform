package id.go.govedu.assist.dto.delegation;

import java.time.LocalDateTime;
import java.util.UUID;

public record DelegationResponse(
    UUID delegation_id,
    UUID delegator_id,
    UUID delegatee_id,
    LocalDateTime start_date,
    LocalDateTime end_date,
    boolean is_active
) {}
