package id.go.govedu.assist.dto.ekyc;

import java.time.LocalDateTime;
import java.util.UUID;

public record EkycVerifyResponse(
    UUID user_id,
    String nik,
    Boolean ekyc_verified,
    LocalDateTime verified_at
) {}
