package id.go.govedu.assist.dto.bank;

import java.util.UUID;

public record BankAccountResponse(
    UUID id,
    String bank_code,
    String account_number,
    String account_name,
    Boolean is_active
) {}
