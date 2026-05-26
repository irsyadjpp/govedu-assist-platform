package id.go.govedu.assist.model;

import id.go.govedu.assist.audit.ApplicationAuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "application")
@EntityListeners(ApplicationAuditListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserApplicant userApplicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Column(name = "has_active_fraud_flag", nullable = false)
    private Boolean hasActiveFraudFlag = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Document> documents = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApprovalHistory> approvalHistories = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FraudFlag> fraudFlags = new HashSet<>();

    public Application() {
    }

    public Application(UserApplicant userApplicant, Program program) {
        this.userApplicant = userApplicant;
        this.program = program;
    }

    public void addDocument(Document document) {
        documents.add(document);
        document.setApplication(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setApplication(null);
    }

    public void addApprovalHistory(ApprovalHistory approvalHistory) {
        approvalHistories.add(approvalHistory);
        approvalHistory.setApplication(this);
    }

    public void removeApprovalHistory(ApprovalHistory approvalHistory) {
        approvalHistories.remove(approvalHistory);
        approvalHistory.setApplication(null);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setApplication(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setApplication(null);
    }

    public void addFraudFlag(FraudFlag fraudFlag) {
        fraudFlags.add(fraudFlag);
        fraudFlag.setApplication(this);
    }

    public void removeFraudFlag(FraudFlag fraudFlag) {
        fraudFlags.remove(fraudFlag);
        fraudFlag.setApplication(null);
    }

    public enum ApplicationStatus {
        DRAFT,
        SUBMITTED,
        IN_REVIEW,
        APPROVED,
        DISBURSED,
        REJECTED,
        SUSPENDED;

        public boolean isFinal() {
            return switch (this) {
                case APPROVED, DISBURSED, REJECTED -> true;
                default -> false;
            };
        }

        public boolean isEditable() {
            return switch (this) {
                case DRAFT -> true;
                default -> false;
            };
        }
    }
}
