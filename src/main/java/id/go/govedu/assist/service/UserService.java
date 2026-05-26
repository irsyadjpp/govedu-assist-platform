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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserApplicantRepository userApplicantRepository;

    public UserProfileResponse getUserProfile(UUID userId) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get active bank account
        var activeBankAccount = user.getBankAccounts().stream()
                .filter(BankAccount::getIsActive)
                .findFirst()
                .map(bank -> new UserProfileResponse.BankAccountInfo(
                        bank.getId(),
                        bank.getBankCode(),
                        bank.getAccountNumber(),
                        bank.getAccountName()
                ));

        return new UserProfileResponse(
                user.getId(),
                user.getNik(),
                user.getName(),
                user.getEmail(),
                user.getEkycVerified(),
                activeBankAccount.orElse(null),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.name() != null) {
            user.setName(request.name());
        }

        user = userApplicantRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                null,
                user.getName(),
                null,
                null,
                null,
                user.getUpdatedAt()
        );
    }
}
