package id.go.govedu.assist.batch;

import id.go.govedu.assist.service.SftpFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationScheduler {

    private final JobOperator jobOperator;
    private final Job reconciliationJob;
    private final SftpFileService sftpFileService;

    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Jakarta")
    public void runDailyReconciliation() {
        log.info("Triggering daily reconciliation job at 06:00 WIB");

        try {
            // Download reconciliation file from SFTP
            String filePath = sftpFileService.downloadReconciliationFile();

            if (filePath == null) {
                log.info("No reconciliation file found, skipping job");
                return;
            }

            // Run reconciliation job
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", filePath)
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            jobOperator.start(reconciliationJob, params);

            // Archive file after processing
            String fileName = new java.io.File(filePath).getName();
            sftpFileService.archiveFile("/inbound/" + fileName);

        } catch (Exception e) {
            log.error("Failed to run daily reconciliation job", e);
        }
    }
}
