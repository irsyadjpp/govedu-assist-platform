package id.go.govedu.assist.dto.bank;

public record BankTransferResponse(
    String responseCode,
    String responseMessage,
    String partnerReferenceNo,
    String referenceNo,
    BankAmount amount,
    String status
) {}
