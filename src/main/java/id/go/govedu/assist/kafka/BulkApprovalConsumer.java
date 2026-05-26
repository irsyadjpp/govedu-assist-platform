package id.go.govedu.assist.kafka;

import id.go.govedu.assist.model.Admin;
import id.go.govedu.assist.model.ApprovalHistory;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.DisbursementBatch;
import id.go.govedu.assist.repository.AdminRepository;
import id.go.govedu.assist.repository.ApplicationRepository;
import id.go.govedu.assist.repository.DisbursementBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BulkApprovalConsumer {

    private final ApplicationRepository applicationRepository;
    private final DisbursementBatchRepository disbursementBatchRepository;
    private final AdminRepository adminRepository;

    @KafkaListener(topics = "bulk-approval-tasks", groupId = "govedu-consumer-group")
    @Transactional
    public void processBulkApproval(BulkApprovalEvent event) {
        log.info("Processing bulk approval for batch: {}", event.batchId());

        var batch = disbursementBatchRepository.findById(event.batchId())
                .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

        var admin = adminRepository.findById(event.adminId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        int processedCount = 0;

        try {
            for (UUID applicationId : event.applicationIds()) {
                var application = applicationRepository.findById(applicationId)
                        .orElse(null);

                if (application == null) {
                    log.warn("Application not found: {}", applicationId);
                    continue;
                }

                // Skip if already approved
                if (application.getStatus() == Application.ApplicationStatus.APPROVED) {
                    log.info("Application already approved: {}", applicationId);
                    processedCount++;
                    continue;
                }

                // Update application status
                application.setStatus(Application.ApplicationStatus.APPROVED);

                // Create approval history
                var approvalHistory = new ApprovalHistory();
                approvalHistory.setApplication(application);
                approvalHistory.setAdmin(admin);
                approvalHistory.setAction("BULK_APPROVED");
                approvalHistory.setNotes(event.notes());
                application.addApprovalHistory(approvalHistory);

                applicationRepository.save(application);

                processedCount++;

                // Update processed count periodically
                if (processedCount % 100 == 0) {
                    batch.setProcessedCount(processedCount);
                    disbursementBatchRepository.save(batch);
                }
            }

            // Final update
            batch.setProcessedCount(processedCount);
            batch.setStatus(DisbursementBatch.BatchStatus.COMPLETED);
            disbursementBatchRepository.save(batch);

            log.info("Bulk approval completed for batch: {}. Processed: {} applications",
                    event.batchId(), processedCount);

        } catch (Exception e) {
            log.error("Error processing bulk approval for batch: {}", event.batchId(), e);
            batch.setStatus(DisbursementBatch.BatchStatus.FAILED);
            disbursementBatchRepository.save(batch);
        }
    }
}
