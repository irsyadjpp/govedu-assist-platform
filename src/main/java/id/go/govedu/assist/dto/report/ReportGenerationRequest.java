package id.go.govedu.assist.dto.report;

import java.util.Map;

public record ReportGenerationRequest(
    String reportType,
    Map<String, Object> filters
) {}
