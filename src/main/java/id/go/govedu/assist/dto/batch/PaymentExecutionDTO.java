package id.go.govedu.assist.dto.batch;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentExecutionDTO(
    UUID paymentId,
    String idempotencyKey,
    String accountNumber,
    BigDecimal amount,
    String beneficiaryEmail
) {}
