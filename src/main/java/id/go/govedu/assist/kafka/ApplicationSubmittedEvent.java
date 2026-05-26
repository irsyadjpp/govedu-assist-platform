package id.go.govedu.assist.kafka;

import java.util.UUID;

public record ApplicationSubmittedEvent(
    UUID applicationId,
    String nik
) {}
