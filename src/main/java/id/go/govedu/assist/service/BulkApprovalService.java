package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.bulk.BulkApprovalRequest;
import id.go.govedu.assist.dto.bulk.BulkApprovalResponse;
import id.go.govedu.assist.dto.bulk.BulkApprovalStatusResponse;
import id.go.govedu.assist.kafka.BulkApprovalEvent;
import id.go.govedu.assist.model.DisbursementBatch;
import id.go.govedu.assist.repository.DisbursementBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BulkApprovalService {

    private final DisbursementBatchRepository disbursementBatchRepository;
    private final KafkaTemplate<String, BulkApprovalEvent> kafkaTemplate;

    @Transactional
    public BulkApprovalResponse initiateBulkApproval(UUID adminId, BulkApprovalRequest request) {
        // Generate batch number
        var batchNumber = "BATCH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        // Create disbursement batch record
        var batch = new DisbursementBatch();
        batch.setBatchNumber(batchNumber);
        batch.setTotalAmount(BigDecimal.ZERO);
        batch.setTotalApplications(request.application_ids().size());
        batch.setProcessedCount(0);
        batch.setStatus(DisbursementBatch.BatchStatus.PROCESSING);
        batch.setScheduledAt(LocalDateTime.now());

        batch = disbursementBatchRepository.save(batch);

        // Publish event to Kafka
        var event = new BulkApprovalEvent(
                batch.getId(),
                request.application_ids(),
                adminId,
                request.notes()
        );
        kafkaTemplate.send("bulk-approval-tasks", event);

        return new BulkApprovalResponse(
                batch.getId(),
                batch.getBatchNumber(),
                batch.getTotalApplications(),
                batch.getStatus().name(),
                "/api/v1/admin/applications/bulk-approve/" + batch.getId() + "/status"
        );
    }

    public BulkApprovalStatusResponse getBulkApprovalStatus(UUID batchId) {
        var batch = disbursementBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

        double progressPercentage = batch.getTotalApplications() > 0
                ? (batch.getProcessedCount() * 100.0) / batch.getTotalApplications()
                : 0.0;

        return new BulkApprovalStatusResponse(
                batch.getId(),
                batch.getBatchNumber(),
                batch.getTotalApplications(),
                batch.getProcessedCount(),
                progressPercentage,
                batch.getStatus().name(),
                batch.getCreatedAt()
        );
    }
}
