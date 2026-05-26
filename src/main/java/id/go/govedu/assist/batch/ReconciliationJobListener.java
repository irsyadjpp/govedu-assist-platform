package id.go.govedu.assist.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReconciliationJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting reconciliation job with ID: {}", jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Reconciliation job failed with status: {}", jobExecution.getStatus());
        } else {
            log.info("Reconciliation job completed successfully. Processed {} items in {} ms",
                    jobExecution.getStepExecutions().stream().mapToLong(se -> se.getWriteCount()).sum(),
                    jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());
        }
    }
}
