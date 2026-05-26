package id.go.govedu.assist.service;

import id.go.govedu.assist.kafka.AuditEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventPublisher {

    private final KafkaTemplate<String, AuditEventMessage> kafkaTemplate;
    private static final String TOPIC = "govedu-audit-events";

    @Async
    public void publishEvent(String entityName, UUID entityId, String action,
                             Map<String, Object> payloadBefore, Map<String, Object> payloadAfter) {
        try {
            String actorId = SecurityUtils.getCurrentUserId();
            String ipAddress = SecurityUtils.getCurrentClientIp();

            AuditEventMessage event = new AuditEventMessage(
                    UUID.randomUUID(),
                    entityName,
                    entityId,
                    action,
                    actorId != null ? UUID.fromString(actorId) : null,
                    ipAddress,
                    payloadBefore,
                    payloadAfter,
                    LocalDateTime.now()
            );

            kafkaTemplate.send(TOPIC, entityId.toString(), event);
            log.debug("Audit event published for {}: {}", entityName, entityId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for {}: {}", entityName, entityId, e);
        }
    }
}
