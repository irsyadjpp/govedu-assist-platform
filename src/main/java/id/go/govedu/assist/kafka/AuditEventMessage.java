package id.go.govedu.assist.kafka;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AuditEventMessage(
    UUID eventId,
    String entityName,
    UUID entityId,
    String action,
    UUID actorId,
    String ipAddress,
    Map<String, Object> payloadBefore,
    Map<String, Object> payloadAfter,
    LocalDateTime timestamp
) {}
