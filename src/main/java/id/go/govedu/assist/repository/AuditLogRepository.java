package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntityNameAndEntityId(String entityName, UUID entityId);

    List<AuditLog> findByActorId(UUID actorId);

    List<AuditLog> findByAction(AuditLog.AuditAction action);

    List<AuditLog> findByEntityName(String entityName);

    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByIpAddress(String ipAddress);

    List<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, UUID entityId);

    @QueryHints(value = {
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, value = "1000"),
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_READONLY, value = "true")
    })
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.createdAt BETWEEN :startDate AND :endDate")
    Stream<AuditLog> streamAuditLogsByDateRange(
            @Param("entityName") String entityName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
