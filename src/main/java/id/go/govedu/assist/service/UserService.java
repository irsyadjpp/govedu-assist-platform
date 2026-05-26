package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.user.UpdateProfileRequest;
import id.go.govedu.assist.dto.user.UserProfileResponse;
import id.go.govedu.assist.model.BankAccount;
import id.go.govedu.assist.model.UserApplicant;
import id.go.govedu.assist.repository.UserApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserApplicantRepository userApplicantRepository;

    public UserProfileResponse getUserProfile(UUID userId) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileResponse response = mapToUserProfileResponse(user);

        // Get active bank account
        Optional<BankAccount> activeBankAccount = user.getBankAccounts().stream()
                .filter(BankAccount::getIsActive)
                .findFirst();

        activeBankAccount.ifPresent(bank -> {
            UserProfileResponse.BankAccountInfo bankInfo = new UserProfileResponse.BankAccountInfo();
            bankInfo.setId(bank.getId());
            bankInfo.setBank_code(bank.getBankCode());
            bankInfo.setAccount_number(bank.getAccountNumber());
            bankInfo.setAccount_name(bank.getAccountName());
            response.setActive_bank_account(bankInfo);
        });

        return response;
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        user = userApplicantRepository.save(user);

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUpdated_at(user.getUpdatedAt());

        return response;
    }

    private UserProfileResponse mapToUserProfileResponse(UserApplicant user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setNik(user.getNik());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setEkyc_verified(user.getEkycVerified());
        response.setCreated_at(user.getCreatedAt());
        response.setUpdated_at(user.getUpdatedAt());
        return response;
    }
}
