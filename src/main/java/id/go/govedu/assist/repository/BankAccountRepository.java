package id.go.govedu.assist.repository;

import id.go.govedu.assist.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    List<BankAccount> findByUserApplicantId(UUID userId);

    List<BankAccount> findByUserApplicantIdAndIsActiveTrue(UUID userId);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findByBankCode(String bankCode);

    List<BankAccount> findByIsActiveTrue();

    @Query("SELECT b.userApplicant.id FROM BankAccount b WHERE b.accountNumber = :accountNo AND b.bankCode = :bankCode AND b.isActive = true")
    List<UUID> findUserIdsByAccountNumberAndBankCode(@Param("accountNo") String accountNo, @Param("bankCode") String bankCode);
}
