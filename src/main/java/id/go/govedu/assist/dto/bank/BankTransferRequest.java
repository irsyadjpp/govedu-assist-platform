package id.go.govedu.assist.dto.bank;

import java.time.OffsetDateTime;

public record BankTransferRequest(
    String partnerReferenceNo,
    BankAmount amount,
    String beneficiaryAccountNo,
    String beneficiaryEmail,
    String sourceAccountNo,
    OffsetDateTime transactionDate,
    String remark
) {}
