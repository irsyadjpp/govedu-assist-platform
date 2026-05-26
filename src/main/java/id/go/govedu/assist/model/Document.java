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
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private DocumentType docType;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Document() {
    }

    public Document(Application application, DocumentType docType, String fileUrl) {
        this.application = application;
        this.docType = docType;
        this.fileUrl = fileUrl;
    }

    public enum DocumentType {
        KTP,
        KK,
        TRANSCRIPT;

        public boolean isRequired() {
            return switch (this) {
                case KTP, KK -> true;
                default -> false;
            };
        }

        public String getDisplayName() {
            return switch (this) {
                case KTP -> "Kartu Tanda Penduduk";
                case KK -> "Kartu Keluarga";
                case TRANSCRIPT -> "Transcript Nilai";
            };
        }
    }
}
