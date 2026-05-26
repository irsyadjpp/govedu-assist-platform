package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.auth.AuthResponse;
import id.go.govedu.assist.dto.auth.LoginRequest;
import id.go.govedu.assist.dto.auth.RegisterRequest;
import id.go.govedu.assist.dto.user.UserProfileResponse;
import id.go.govedu.assist.exception.AuthException;
import id.go.govedu.assist.model.UserApplicant;
import id.go.govedu.assist.repository.UserApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserApplicantRepository userApplicantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (userApplicantRepository.existsByNik(request.getNik())) {
            throw new AuthException("AUTH_CONFLICT", "NIK is already registered");
        }

        if (userApplicantRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("AUTH_CONFLICT", "Email is already registered");
        }

        UserApplicant user = new UserApplicant();
        user.setNik(request.getNik());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEkycVerified(false);

        user = userApplicantRepository.save(user);

        return mapToUserProfileResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        UserApplicant user = userApplicantRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("AUTH_INVALID_CREDENTIALS", "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("AUTH_INVALID_CREDENTIALS", "Invalid email or password");
        }

        // TODO: Implement JWT token generation
        // For now, return mock tokens
        return new AuthResponse(
                "mock_access_token_" + UUID.randomUUID(),
                "mock_refresh_token_" + UUID.randomUUID(),
                3600L,
                "Bearer"
        );
    }

    private UserProfileResponse mapToUserProfileResponse(UserApplicant user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNik(),
                user.getName(),
                user.getEmail(),
                user.getEkycVerified(),
                null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
