package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.DisbursementBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DisbursementBatchRepository extends JpaRepository<DisbursementBatch, UUID> {
}
