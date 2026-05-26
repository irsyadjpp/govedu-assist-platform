package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntityNameAndEntityId(String entityName, UUID entityId);

    List<AuditLog> findByActorId(UUID actorId);

    List<AuditLog> findByAction(AuditLog.AuditAction action);

    List<AuditLog> findByEntityName(String entityName);

    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByIpAddress(String ipAddress);

    List<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, UUID entityId);
}
