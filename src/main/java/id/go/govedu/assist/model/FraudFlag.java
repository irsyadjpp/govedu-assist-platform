package id.go.govedu.assist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "fraud_flag")
public class FraudFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "flag_type", nullable = false)
    private FlagType flagType;

    @Column(name = "external_source")
    private String externalSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlagStatus status = FlagStatus.UNRESOLVED;

    @Column(name = "details", columnDefinition = "JSONB")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    private Admin resolvedByAdmin;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FraudFlag() {
    }

    public FraudFlag(Application application, FlagType flagType, String externalSource) {
        this.application = application;
        this.flagType = flagType;
        this.externalSource = externalSource;
    }

    public enum FlagType {
        DOUBLE_FUNDING,
        DUPLICATE_BANK,
        NIK_MISMATCH;

        public String getDescription() {
            return switch (this) {
                case DOUBLE_FUNDING -> "Double funding detected";
                case DUPLICATE_BANK -> "Duplicate bank account detected";
                case NIK_MISMATCH -> "NIK mismatch detected";
            };
        }
    }

    public enum FlagStatus {
        UNRESOLVED,
        FALSE_ALARM,
        CONFIRMED;

        public boolean isFinal() {
            return switch (this) {
                case FALSE_ALARM, CONFIRMED -> true;
                default -> false;
            };
        }
    }
}
