package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.bank.BankAccountRequest;
import id.go.govedu.assist.dto.bank.BankAccountResponse;
import id.go.govedu.assist.model.BankAccount;
import id.go.govedu.assist.model.UserApplicant;
import id.go.govedu.assist.repository.BankAccountRepository;
import id.go.govedu.assist.repository.UserApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserApplicantRepository userApplicantRepository;

    @Transactional
    public BankAccountResponse registerBankAccount(UUID userId, BankAccountRequest request) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Deactivate all existing bank accounts for this user
        user.getBankAccounts().forEach(bank -> bank.setIsActive(false));
        bankAccountRepository.saveAll(user.getBankAccounts());

        // Create new bank account
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserApplicant(user);
        bankAccount.setBankCode(request.getBank_code());
        bankAccount.setAccountNumber(request.getAccount_number());
        bankAccount.setAccountName(request.getAccount_name());
        bankAccount.setIsActive(true);

        bankAccount = bankAccountRepository.save(bankAccount);

        return mapToBankAccountResponse(bankAccount);
    }

    private BankAccountResponse mapToBankAccountResponse(BankAccount bankAccount) {
        return new BankAccountResponse(
                bankAccount.getId(),
                bankAccount.getBankCode(),
                bankAccount.getAccountNumber(),
                bankAccount.getAccountName(),
                bankAccount.getIsActive()
        );
    }
}
