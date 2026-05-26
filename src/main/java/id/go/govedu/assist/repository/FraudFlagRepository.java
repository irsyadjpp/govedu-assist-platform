package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.FraudFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudFlagRepository extends JpaRepository<FraudFlag, UUID> {

    List<FraudFlag> findByApplicationId(UUID applicationId);

    List<FraudFlag> findByApplicationIdAndIsResolvedFalse(UUID applicationId);

    List<FraudFlag> findByAnomalyType(FraudFlag.AnomalyType anomalyType);

    List<FraudFlag> findBySeverity(FraudFlag.Severity severity);

    List<FraudFlag> findByIsResolvedFalse();

    List<FraudFlag> findBySeverityAndIsResolvedFalse(FraudFlag.Severity severity);
}
