package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.UserApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserApplicantRepository extends JpaRepository<UserApplicant, UUID> {

    Optional<UserApplicant> findByNik(String nik);

    Optional<UserApplicant> findByEmail(String email);

    boolean existsByNik(String nik);

    boolean existsByEmail(String email);

    Optional<UserApplicant> findByNikAndEkycVerifiedTrue(String nik);
}
