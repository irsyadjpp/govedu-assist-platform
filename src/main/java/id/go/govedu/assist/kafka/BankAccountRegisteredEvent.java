package id.go.govedu.assist.kafka;

import java.util.UUID;

public record BankAccountRegisteredEvent(
    UUID userId,
    String accountNumber,
    String bankCode
) {}
