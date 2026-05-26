package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.DisbursementBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisbursementBatchRepository extends JpaRepository<DisbursementBatch, UUID> {

    Optional<DisbursementBatch> findByBatchNumber(String batchNumber);

    boolean existsByBatchNumber(String batchNumber);

    List<DisbursementBatch> findByStatus(DisbursementBatch.BatchStatus status);

    List<DisbursementBatch> findByScheduledAtBefore(LocalDateTime dateTime);

    List<DisbursementBatch> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
