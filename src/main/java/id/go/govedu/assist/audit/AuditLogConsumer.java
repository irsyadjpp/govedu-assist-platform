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
            AuditLog log = new AuditLog();
            log.setEntityName(message.entityName());
            log.setEntityId(message.entityId());
            log.setAction(AuditLog.AuditAction.valueOf(message.action()));
            log.setActorId(message.actorId());
            log.setIpAddress(message.ipAddress());
            log.setPayloadBefore(message.payloadBefore());
            log.setPayloadAfter(message.payloadAfter());

            auditLogRepository.save(log);
            log.debug("Audit log saved for {}: {}", message.entityName(), message.entityId());
        } catch (Exception e) {
            log.error("Failed to save audit log for {}: {}", message.entityName(), message.entityId(), e);
        }
    }
}
