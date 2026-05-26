package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.FraudFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudFlagRepository extends JpaRepository<FraudFlag, UUID> {

    List<FraudFlag> findByApplicationId(UUID applicationId);

    List<FraudFlag> findByApplicationIdAndStatus(UUID applicationId, FraudFlag.FlagStatus status);

    List<FraudFlag> findByFlagType(FraudFlag.FlagType flagType);

    Page<FraudFlag> findByFlagType(FraudFlag.FlagType flagType, Pageable pageable);

    List<FraudFlag> findByStatus(FraudFlag.FlagStatus status);

    List<FraudFlag> findByStatusNot(FraudFlag.FlagStatus status);
}
