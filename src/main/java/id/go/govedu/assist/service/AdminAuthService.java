package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.admin.AdminLoginRequest;
import id.go.govedu.assist.dto.admin.AdminLoginResponse;
import id.go.govedu.assist.dto.admin.AdminProfile;
import id.go.govedu.assist.exception.AuthException;
import id.go.govedu.assist.model.Admin;
import id.go.govedu.assist.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByNip(request.nip())
                .orElseThrow(() -> new AuthException("AUTH_INVALID_CREDENTIALS", "Invalid NIP or password"));

        if (!admin.getIsActive()) {
            throw new AuthException("AUTH_ACCOUNT_DISABLED", "Admin account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new AuthException("AUTH_INVALID_CREDENTIALS", "Invalid NIP or password");
        }

        String accessToken = jwtService.generateAdminToken(
                admin.getId(),
                admin.getNip(),
                admin.getRoleTier().name(),
                admin.getInstitutionCode()
        );

        AdminProfile profile = new AdminProfile(
                admin.getId(),
                admin.getName(),
                admin.getRoleTier().name(),
                admin.getInstitutionCode()
        );

        return new AdminLoginResponse(accessToken, 7200L, profile);
    }
}
