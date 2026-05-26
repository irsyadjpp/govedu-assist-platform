package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, UUID> {

    List<ApprovalHistory> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);

    List<ApprovalHistory> findByAdminId(UUID adminId);

    List<ApprovalHistory> findByApplicationIdAndAdminId(UUID applicationId, UUID adminId);
}
