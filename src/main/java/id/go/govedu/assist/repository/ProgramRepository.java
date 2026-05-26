package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {

    List<Program> findByIsActiveTrue();

    List<Program> findByStartDateBeforeAndEndDateAfter(LocalDate date, LocalDate date2);

    Optional<Program> findByName(String name);

    boolean existsByName(String name);
}
