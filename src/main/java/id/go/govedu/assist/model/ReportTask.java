package id.go.govedu.assist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "report_task")
public class ReportTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filter_params", columnDefinition = "jsonb")
    private Map<String, Object> filterParams;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false, nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public ReportTask() {
    }

    public ReportTask(Admin admin, ReportType reportType, Map<String, Object> filterParams) {
        this.admin = admin;
        this.reportType = reportType;
        this.filterParams = filterParams;
    }

    public enum ReportType {
        AUDIT_LOG_CSV,
        DISBURSEMENT_EXCEL,
        APPLICATION_CSV
    }

    public enum TaskStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
