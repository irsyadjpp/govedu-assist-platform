package id.go.govedu.assist.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class DisbursementJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting disbursement job with ID: {}", jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Disbursement job failed with status: {}", jobExecution.getStatus());
        } else {
            long durationMs = Duration.between(
                    jobExecution.getStartTime(),
                    jobExecution.getEndTime()
            ).toMillis();

            log.info("Disbursement job completed successfully. Processed {} items in {} ms",
                    jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum(),
                    durationMs);
        }
    }
}
