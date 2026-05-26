package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.DisbursementBatch;
import id.go.govedu.assist.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByBankReferenceNo(String bankReferenceNo);

    List<Payment> findByDisbursementBatch(DisbursementBatch disbursementBatch);

    List<Payment> findByApplicationId(UUID applicationId);

    List<Payment> findByBankAccountId(UUID bankAccountId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByDisbursementBatchAndStatus(DisbursementBatch disbursementBatch, Payment.PaymentStatus status);
}
