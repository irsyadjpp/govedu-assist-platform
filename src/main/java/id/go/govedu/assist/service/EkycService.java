package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.ekyc.DukcapilRequest;
import id.go.govedu.assist.dto.ekyc.DukcapilResponse;
import id.go.govedu.assist.dto.ekyc.EkycVerifyRequest;
import id.go.govedu.assist.dto.ekyc.EkycVerifyResponse;
import id.go.govedu.assist.exception.KycException;
import id.go.govedu.assist.fraud.client.DukcapilClient;
import id.go.govedu.assist.model.UserApplicant;
import id.go.govedu.assist.repository.UserApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EkycService {

    private final DukcapilClient dukcapilClient;
    private final UserApplicantRepository userApplicantRepository;

    @Transactional
    public EkycVerifyResponse verifyIdentity(UUID userId, EkycVerifyRequest request) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate NIK matches user's registered NIK
        if (!user.getNik().equals(request.nik())) {
            throw new KycException("KYC_NIK_MISMATCH", "NIK does not match registered account");
        }

        // Convert birth date to Dukcapil format (DD-MM-YYYY)
        var birthDate = LocalDate.parse(request.birth_date());
        var dukcapilBirthDate = birthDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Build Dukcapil request
        var dukcapilRequest = new DukcapilRequest(
                request.nik(),
                request.full_name().toUpperCase(),
                request.birth_place().toUpperCase(),
                dukcapilBirthDate
        );

        // Call Dukcapil API
        DukcapilResponse response;
        try {
            response = dukcapilClient.verifyIdentity(dukcapilRequest);
        } catch (Exception e) {
            throw new KycException("KYC_SYSTEM_ERROR", "Failed to connect to Dukcapil service");
        }

        // Check for system error
        if (response.status().equals("9999")) {
            throw new KycException("KYC_SYSTEM_ERROR", response.pesan());
        }

        // Validate data match
        if (response.data() == null) {
            throw new KycException("KYC_DATA_MISMATCH", "Data does not match with national registry (Dukcapil). Please check your input.");
        }

        var data = response.data();
        if (!data.NIK().equals("Sesuai") ||
            !data.NAMA_LGKP().equals("Sesuai") ||
            !data.TMPT_LHR().equals("Sesuai") ||
            !data.TGL_LHR().equals("Sesuai")) {
            throw new KycException("KYC_DATA_MISMATCH", "Data does not match with national registry (Dukcapil). Please check your input.");
        }

        // Update user eKYC status
        user.setEkycVerified(true);
        user = userApplicantRepository.save(user);

        return new EkycVerifyResponse(
                user.getId(),
                user.getNik(),
                user.getEkycVerified(),
                user.getUpdatedAt()
        );
    }
}
