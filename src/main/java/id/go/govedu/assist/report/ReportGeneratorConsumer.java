package id.go.govedu.assist.report;

import id.go.govedu.assist.kafka.ReportTaskMessage;
import id.go.govedu.assist.model.ReportTask;
import id.go.govedu.assist.repository.AuditLogRepository;
import id.go.govedu.assist.repository.ReportTaskRepository;
import id.go.govedu.assist.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGeneratorConsumer {

    private final ReportTaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;
    private final S3StorageService s3StorageService;

    @KafkaListener(topics = "report-generation-tasks", groupId = "report-worker-group")
    @Transactional(readOnly = true)
    public void generateReport(ReportTaskMessage message) {
        ReportTask task = taskRepository.findById(message.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Report task not found"));

        task.setStatus(ReportTask.TaskStatus.PROCESSING);
        taskRepository.save(task);

        try {
            File tempFile = File.createTempFile("audit_report_", ".csv");

            try (FileWriter fw = new FileWriter(tempFile);
                 CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT.withHeader("ID", "Action", "Actor", "IP", "Date", "Entity", "EntityID"))) {

                Map<String, Object> filters = message.filterParams();
                String entityName = (String) filters.get("entity_name");
                LocalDateTime startDate = LocalDateTime.parse((String) filters.get("start_date"));
                LocalDateTime endDate = LocalDateTime.parse((String) filters.get("end_date"));

                try (var auditStream = auditLogRepository.streamAuditLogsByDateRange(entityName, startDate, endDate)) {
                    auditStream.forEach(log -> {
                        try {
                            csvPrinter.printRecord(
                                    log.getId(),
                                    log.getAction(),
                                    log.getActorId(),
                                    log.getIpAddress(),
                                    log.getCreatedAt(),
                                    log.getEntityName(),
                                    log.getEntityId()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException("Error writing CSV row", e);
                        }
                    });
                }
            }

            String s3Url = s3StorageService.uploadFile("reports", tempFile);

            task.setStatus(ReportTask.TaskStatus.COMPLETED);
            task.setFileUrl(s3Url);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);

            tempFile.delete();

            log.info("Report generation completed for task: {}", task.getId());

        } catch (Exception e) {
            task.setStatus(ReportTask.TaskStatus.FAILED);
            task.setFailureReason(e.getMessage());
            taskRepository.save(task);
            log.error("Report generation failed for task: {}", task.getId(), e);
        }
    }
}
