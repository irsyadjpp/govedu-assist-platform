package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    List<Application> findByUserApplicantId(UUID userId);

    List<Application> findByProgramId(UUID programId);

    List<Application> findByStatus(Application.ApplicationStatus status);

    List<Application> findByUserApplicantIdAndStatus(UUID userId, Application.ApplicationStatus status);

    List<Application> findByProgramIdAndStatus(UUID programId, Application.ApplicationStatus status);

    Optional<Application> findByUserApplicantIdAndProgramId(UUID userId, UUID programId);

    long countByProgramId(UUID programId);

    long countByStatus(Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE (a.status = 'SUBMITTED' OR a.status = 'IN_REVIEW') AND a.updatedAt < :threshold")
    List<Application> findSlaBreachedApplications(@Param("threshold") LocalDateTime threshold);

    Optional<Application> findActiveApplicationByUserId(UUID userId);
}
