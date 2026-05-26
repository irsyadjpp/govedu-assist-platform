package id.go.govedu.assist.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileResponse(
    UUID id,
    String nik,
    String name,
    String email,
    Boolean ekyc_verified,
    BankAccountInfo active_bank_account,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BankAccountInfo(
        UUID id,
        String bank_code,
        String account_number,
        String account_name
    ) {}
}
