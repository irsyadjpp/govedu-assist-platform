package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.bank.BankAmount;
import id.go.govedu.assist.dto.bank.BankTransferRequest;
import id.go.govedu.assist.dto.bank.BankTransferResponse;
import id.go.govedu.assist.model.Payment;
import id.go.govedu.assist.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankTransferService {

    private final RestClient bankHimbaraClient;
    private final PaymentRepository paymentRepository;

    @Value("${bank.himbara.source-account-number:}")
    private String sourceAccountNumber;

    @Transactional
    public Payment executeTransfer(UUID paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Prepare bank transfer request
        var bankRequest = new BankTransferRequest(
                payment.getIdempotencyKey(),
                new BankAmount(
                        payment.getAmount().toString(),
                        "IDR"
                ),
                payment.getBankAccount().getAccountNumber(),
                payment.getApplication().getUserApplicant().getEmail(),
                sourceAccountNumber,
                OffsetDateTime.now(),
                "Pencairan Beasiswa KIP-K"
        );

        try {
            // Call bank API
            var bankResponse = bankHimbaraClient.post()
                    .uri("/fund-transfer/inter-bank")
                    .body(bankRequest)
                    .retrieve()
                    .body(BankTransferResponse.class);

            log.info("Bank transfer response: {}", bankResponse);

            // Update payment based on response
            if ("2000000".equals(bankResponse.responseCode())) {
                payment.setStatus(Payment.PaymentStatus.PROCESSED);
                payment.setBankReferenceNo(bankResponse.referenceNo());
                payment.setProcessedAt(java.time.LocalDateTime.now());
            } else if ("2000100".equals(bankResponse.responseCode())) {
                // Pending status
                payment.setStatus(Payment.PaymentStatus.PENDING_VERIFICATION);
                payment.setBankReferenceNo(bankResponse.referenceNo());
            } else {
                // Failed
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(bankResponse.responseMessage());
            }

            return paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Bank transfer failed for payment: {}", paymentId, e);
            // Set to PENDING_VERIFICATION for reconciliation
            payment.setStatus(Payment.PaymentStatus.PENDING_VERIFICATION);
            payment.setFailureReason("Timeout or network error - pending reconciliation");
            return paymentRepository.save(payment);
        }
    }
}
