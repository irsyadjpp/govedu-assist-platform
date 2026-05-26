package id.go.govedu.assist.kafka;

import java.util.Map;
import java.util.UUID;

public record ReportTaskMessage(
    UUID taskId,
    String reportType,
    Map<String, Object> filterParams
) {}
