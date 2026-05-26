package id.go.govedu.assist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "disbursement_batch")
public class DisbursementBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "batch_number", unique = true, nullable = false)
    private String batchNumber;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_applications", nullable = false)
    private Integer totalApplications;

    @Column(name = "processed_count", nullable = false)
    private Integer processedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status = BatchStatus.PENDING;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "disbursementBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    public DisbursementBatch() {
    }

    public DisbursementBatch(String batchNumber, BigDecimal totalAmount, Integer totalApplications, LocalDateTime scheduledAt) {
        this.batchNumber = batchNumber;
        this.totalAmount = totalAmount;
        this.totalApplications = totalApplications;
        this.scheduledAt = scheduledAt;
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setDisbursementBatch(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setDisbursementBatch(null);
    }

    public enum BatchStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED;

        public boolean isFinal() {
            return switch (this) {
                case COMPLETED, FAILED -> true;
                default -> false;
            };
        }

        public boolean isProcessing() {
            return switch (this) {
                case PROCESSING -> true;
                default -> false;
            };
        }
    }
}
