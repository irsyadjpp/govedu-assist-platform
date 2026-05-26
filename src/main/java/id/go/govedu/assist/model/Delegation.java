package id.go.govedu.assist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "delegation")
public class Delegation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegator_id", nullable = false)
    private Admin delegator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegatee_id", nullable = false)
    private Admin delegatee;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Delegation() {
    }

    public Delegation(Admin delegator, Admin delegatee, LocalDateTime startDate, LocalDateTime endDate) {
        this.delegator = delegator;
        this.delegatee = delegatee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
