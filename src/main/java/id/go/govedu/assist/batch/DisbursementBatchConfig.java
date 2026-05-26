package id.go.govedu.assist.batch;

import id.go.govedu.assist.dto.batch.PaymentExecutionDTO;
import id.go.govedu.assist.model.Payment;
import id.go.govedu.assist.repository.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DisbursementBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final PaymentRepository paymentRepository;

    @Bean
    public ItemReader<Payment> paymentItemReader() {
        return new JpaPagingItemReaderBuilder<Payment>()
                .name("paymentItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM Payment p WHERE p.status = 'QUEUED' ORDER BY p.createdAt ASC")
                .pageSize(500)
                .build();
    }

    @Bean
    public ItemProcessor<Payment, PaymentExecutionDTO> paymentItemProcessor() {
        return payment -> {
            // Check if bank account is active
            if (payment.getBankAccount() != null && !payment.getBankAccount().getIsActive()) {
                log.warn("Bank account deactivated for payment: {}", payment.getId());
                return null; // Skip this item
            }

            // Map to DTO for bank API
            return new PaymentExecutionDTO(
                    payment.getId(),
                    payment.getIdempotencyKey(),
                    payment.getBankAccount().getAccountNumber(),
                    payment.getAmount(),
                    payment.getApplication().getUserApplicant().getEmail()
            );
        };
    }

    @Bean
    public ItemWriter<PaymentExecutionDTO> paymentItemWriter() {
        return chunk -> {
            for (PaymentExecutionDTO dto : chunk) {
                try {
                    // Call bank API (Task 8)
                    // BankTransferService.executeTransfer(dto.paymentId())
                    
                    // Update payment status
                    var payment = paymentRepository.findById(dto.paymentId())
                            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
                    
                    payment.setStatus(Payment.PaymentStatus.PENDING_VERIFICATION);
                    paymentRepository.save(payment);
                    
                    log.info("Processed payment: {}", dto.paymentId());
                } catch (Exception e) {
                    log.error("Failed to process payment: {}", dto.paymentId(), e);
                    
                    // Update to failed status
                    var payment = paymentRepository.findById(dto.paymentId())
                            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
                    
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason(e.getMessage());
                    paymentRepository.save(payment);
                }
            }
        };
    }

    @Bean
    public Step processPaymentStep() {
        return new ChunkOrientedStepBuilder<Payment, PaymentExecutionDTO>(
                "processPaymentStep",
                jobRepository,
                500
        )
                .transactionManager(transactionManager)
                .reader(paymentItemReader())
                .processor(paymentItemProcessor())
                .writer(paymentItemWriter())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100)
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Job nightlyDisbursementJob(Step processPaymentStep) {
        return new JobBuilder("nightlyDisbursementJob", jobRepository)
                .listener(new DisbursementJobListener())
                .start(processPaymentStep)
                .build();
    }
}
