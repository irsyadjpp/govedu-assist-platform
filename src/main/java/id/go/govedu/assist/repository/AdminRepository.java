package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByNip(String nip);

    List<Admin> findByInstitutionCode(String institutionCode);
}
