package id.go.govedu.assist.batch;

import id.go.govedu.assist.dto.recon.BankReconLineDTO;
import id.go.govedu.assist.model.Payment;
import id.go.govedu.assist.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ReconciliationBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaymentRepository paymentRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<BankReconLineDTO> reconFileReader(
            @Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<BankReconLineDTO>()
                .name("reconFileReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .delimiter(",")
                .names("partnerReferenceNo", "bankReferenceNo", "amount", "bankStatus", "failureReason", "processedDate")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<BankReconLineDTO>() {{
                    setTargetType(BankReconLineDTO.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<BankReconLineDTO, Payment> reconItemProcessor() {
        return reconLine -> {
            // Find payment by idempotency key
            var payment = paymentRepository.findByIdempotencyKey(reconLine.getPartnerReferenceNo())
                    .orElse(null);

            if (payment == null) {
                log.warn("Payment not found for partner reference: {}", reconLine.getPartnerReferenceNo());
                return null; // Skip this item
            }

            // If status already matches, skip
            String currentStatus = payment.getStatus().name();
            if (currentStatus.equals(reconLine.getBankStatus())) {
                return null;
            }

            // Reconcile: Update status based on bank response
            if ("SUCCESS".equals(reconLine.getBankStatus())) {
                payment.setStatus(Payment.PaymentStatus.PROCESSED);
            } else if ("FAILED".equals(reconLine.getBankStatus())) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Reconciled by EOD File: " + reconLine.getFailureReason());
            }

            payment.setBankReferenceNo(reconLine.getBankReferenceNo());

            return payment;
        };
    }

    @Bean
    public ItemWriter<Payment> reconItemWriter() {
        return updatedPayments -> {
            paymentRepository.saveAll(updatedPayments);
            log.info("Updated {} payments based on reconciliation", updatedPayments.size());
        };
    }

    @Bean
    public Step reconciliationStep() {
        return new ChunkOrientedStepBuilder<BankReconLineDTO, Payment>(
                "reconciliationStep",
                jobRepository,
                500
        )
                .transactionManager(transactionManager)
                .reader(reconFileReader(null))
                .processor(reconItemProcessor())
                .writer(reconItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(50)
                .build();
    }

    @Bean
    public Job reconciliationJob(Step reconciliationStep) {
        return new JobBuilder("reconciliationJob", jobRepository)
                .listener(new ReconciliationJobListener())
                .start(reconciliationStep)
                .build();
    }
}
