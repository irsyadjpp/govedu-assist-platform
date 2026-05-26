package id.go.govedu.assist.model;

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
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nip", unique = true, nullable = false)
    private String nip;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_tier", nullable = false)
    private RoleTier roleTier;

    @Column(name = "institution_code")
    private String institutionCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApprovalHistory> approvalHistories = new HashSet<>();

    @OneToMany(mappedBy = "delegator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delegation> delegationsAsDelegator = new HashSet<>();

    @OneToMany(mappedBy = "delegatee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Delegation> delegationsAsDelegatee = new HashSet<>();

    public Admin() {
    }

    public Admin(String nip, String name, String email, RoleTier roleTier, String institutionCode) {
        this.nip = nip;
        this.name = name;
        this.email = email;
        this.roleTier = roleTier;
        this.institutionCode = institutionCode;
    }

    public void addApprovalHistory(ApprovalHistory approvalHistory) {
        approvalHistories.add(approvalHistory);
        approvalHistory.setAdmin(this);
    }

    public void removeApprovalHistory(ApprovalHistory approvalHistory) {
        approvalHistories.remove(approvalHistory);
        approvalHistory.setAdmin(null);
    }

    public void addDelegationAsDelegator(Delegation delegation) {
        delegationsAsDelegator.add(delegation);
        delegation.setDelegator(this);
    }

    public void removeDelegationAsDelegator(Delegation delegation) {
        delegationsAsDelegator.remove(delegation);
        delegation.setDelegator(null);
    }

    public void addDelegationAsDelegatee(Delegation delegation) {
        delegationsAsDelegatee.add(delegation);
        delegation.setDelegatee(this);
    }

    public void removeDelegationAsDelegatee(Delegation delegation) {
        delegationsAsDelegatee.remove(delegation);
        delegation.setDelegatee(null);
    }

    public enum RoleTier {
        TIER_1_UNIV,
        TIER_2_GOV,
        TIER_3_PPK;

        public int getPriorityLevel() {
            return switch (this) {
                case TIER_1_UNIV -> 1;
                case TIER_2_GOV -> 2;
                case TIER_3_PPK -> 3;
            };
        }

        public boolean canApprove() {
            return switch (this) {
                case TIER_1_UNIV, TIER_2_GOV -> true;
                default -> false;
            };
        }

        public String getDisplayName() {
            return switch (this) {
                case TIER_1_UNIV -> "University Level";
                case TIER_2_GOV -> "Government Level";
                case TIER_3_PPK -> "PPK Level";
            };
        }
    }
}
