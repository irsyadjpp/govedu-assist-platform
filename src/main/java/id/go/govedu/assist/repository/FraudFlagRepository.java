package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.FraudFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FraudFlagRepository extends JpaRepository<FraudFlag, UUID> {

    Page<FraudFlag> findByFlagType(FraudFlag.FlagType flagType, Pageable pageable);
}
