package id.go.govedu.assist.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationScheduler {

    private final JobLauncher jobLauncher;
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

            jobLauncher.run(reconciliationJob, params);

            // Archive file after processing
            String fileName = new java.io.File(filePath).getName();
            sftpFileService.archiveFile("/inbound/" + fileName);

        } catch (Exception e) {
            log.error("Failed to run daily reconciliation job", e);
        }
    }
}
