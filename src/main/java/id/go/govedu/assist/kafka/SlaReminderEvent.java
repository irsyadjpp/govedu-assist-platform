package id.go.govedu.assist.kafka;

import java.util.List;
import java.util.UUID;

public record SlaReminderEvent(
    List<UUID> applicationIds,
    String institutionCode,
    String slaBreachReason
) {}
