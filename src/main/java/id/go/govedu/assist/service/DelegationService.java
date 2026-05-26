package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.delegation.CreateDelegationRequest;
import id.go.govedu.assist.dto.delegation.DelegationResponse;
import id.go.govedu.assist.dto.delegation.RevokeDelegationResponse;
import id.go.govedu.assist.exception.DelegationTierMismatchException;
import id.go.govedu.assist.model.Delegation;
import id.go.govedu.assist.repository.AdminRepository;
import id.go.govedu.assist.repository.DelegationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DelegationService {

    private final DelegationRepository delegationRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public DelegationResponse createDelegation(UUID delegatorId, CreateDelegationRequest request) {
        var delegator = adminRepository.findById(delegatorId)
                .orElseThrow(() -> new IllegalArgumentException("Delegator not found"));

        var delegatee = adminRepository.findByNip(request.delegatee_nip())
                .orElseThrow(() -> new IllegalArgumentException("Delegatee not found"));

        // Validate tier mismatch
        if (delegator.getRoleTier() != delegatee.getRoleTier()) {
            throw new DelegationTierMismatchException();
        }

        var delegation = new Delegation();
        delegation.setDelegator(delegator);
        delegation.setDelegatee(delegatee);
        delegation.setStartDate(request.start_date());
        delegation.setEndDate(request.end_date());
        delegation.setIsActive(true);
        delegation.setNotes(request.notes());

        delegation = delegationRepository.save(delegation);

        return new DelegationResponse(
                delegation.getId(),
                delegation.getDelegator().getId(),
                delegation.getDelegatee().getId(),
                delegation.getStartDate(),
                delegation.getEndDate(),
                delegation.getIsActive()
        );
    }

    @Transactional
    public RevokeDelegationResponse revokeDelegation(UUID delegationId, UUID adminId) {
        var delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new IllegalArgumentException("Delegation not found"));

        // Validate that only the delegator can revoke
        if (!delegation.getDelegator().getId().equals(adminId)) {
            throw new IllegalArgumentException("Only the delegator can revoke this delegation");
        }

        delegation.setIsActive(false);
        delegation = delegationRepository.save(delegation);

        return new RevokeDelegationResponse(
                delegation.getId(),
                delegation.getIsActive(),
                LocalDateTime.now()
        );
    }
}
