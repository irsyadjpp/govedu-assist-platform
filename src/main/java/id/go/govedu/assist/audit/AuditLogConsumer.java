package id.go.govedu.assist.audit;

import id.go.govedu.assist.kafka.AuditEventMessage;
import id.go.govedu.assist.model.AuditLog;
import id.go.govedu.assist.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(topics = "govedu-audit-events", groupId = "audit-storage-group")
    public void consumeAudit(AuditEventMessage message) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityName(message.entityName());
            auditLog.setEntityId(message.entityId());
            auditLog.setAction(AuditLog.AuditAction.valueOf(message.action()));
            auditLog.setActorId(message.actorId());
            auditLog.setIpAddress(message.ipAddress());
            auditLog.setPayloadBefore(message.payloadBefore());
            auditLog.setPayloadAfter(message.payloadAfter());

            auditLogRepository.save(auditLog);
            log.debug("Audit log saved for {}: {}", message.entityName(), message.entityId());
        } catch (Exception e) {
            log.error("Failed to save audit log for {}: {}", message.entityName(), message.entityId(), e);
        }
    }
}
