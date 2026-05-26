package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, UUID> {

    List<Delegation> findByDelegatorId(UUID delegatorId);

    List<Delegation> findByDelegateeId(UUID delegateeId);

    List<Delegation> findByDelegatorIdAndIsActiveTrue(UUID delegatorId);

    List<Delegation> findByDelegateeIdAndIsActiveTrue(UUID delegateeId);

    List<Delegation> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(LocalDateTime date, LocalDateTime date2);

    List<Delegation> findByDelegatorIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(UUID delegatorId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT d FROM Delegation d WHERE d.delegator.id = :adminId AND d.isActive = true AND :currentDate BETWEEN d.startDate AND d.endDate")
    Optional<Delegation> findActiveDelegationForAdmin(@Param("adminId") UUID adminId, @Param("currentDate") LocalDateTime currentDate);
}
