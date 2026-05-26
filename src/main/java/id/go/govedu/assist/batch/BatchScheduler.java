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
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job nightlyDisbursementJob;

    @Scheduled(cron = "0 0 2 * * ?", zone = "Asia/Jakarta")
    public void runNightlyDisbursement() {
        log.info("Triggering nightly disbursement job at 02:00 WIB");

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            jobLauncher.run(nightlyDisbursementJob, params);
        } catch (Exception e) {
            log.error("Failed to run nightly disbursement job", e);
        }
    }
}
