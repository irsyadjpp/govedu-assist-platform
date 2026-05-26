package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.ReportTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportTaskRepository extends JpaRepository<ReportTask, UUID> {
}
