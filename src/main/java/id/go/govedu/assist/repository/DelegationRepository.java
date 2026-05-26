package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, UUID> {

    @Query("SELECT d FROM Delegation d WHERE d.delegator.id = :adminId AND d.isActive = true AND :currentDate BETWEEN d.startDate AND d.endDate")
    Optional<Delegation> findActiveDelegationForAdmin(@Param("adminId") UUID adminId, @Param("currentDate") LocalDateTime currentDate);
}
