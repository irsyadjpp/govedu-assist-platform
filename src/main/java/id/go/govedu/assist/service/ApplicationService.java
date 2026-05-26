package id.go.govedu.assist.service;

import id.go.govedu.assist.dto.application.*;
import id.go.govedu.assist.exception.ReviewerMismatchException;
import id.go.govedu.assist.model.Admin;
import id.go.govedu.assist.model.ApprovalHistory;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.UserApplicant;
import id.go.govedu.assist.repository.AdminRepository;
import id.go.govedu.assist.repository.ApplicationRepository;
import id.go.govedu.assist.repository.ProgramRepository;
import id.go.govedu.assist.repository.UserApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;
    private final UserApplicantRepository userApplicantRepository;
    private final AdminRepository adminRepository;
    private final ApplicationStateMachine stateMachine;

    @Transactional
    public ApplicationResponse createApplication(UUID userId, CreateApplicationRequest request) {
        UserApplicant user = userApplicantRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var program = programRepository.findById(request.program_id())
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        var application = new Application(user, program);
        application.setStatus(Application.ApplicationStatus.DRAFT);

        application = applicationRepository.save(application);

        return ApplicationResponse.fromEntity(application);
    }

    @Transactional
    public SubmitResponse submitApplication(UUID applicationId) {
        var application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Validate guard condition
        stateMachine.validateSubmitGuard(application);

        // Perform transition
        var newStatus = stateMachine.transition(application.getStatus(), ApplicationStateMachine.Event.SUBMIT);
        application.setStatus(newStatus);

        application = applicationRepository.save(application);

        return new SubmitResponse(
                application.getId(),
                application.getStatus().name(),
                application.getUpdatedAt()
        );
    }

    @Transactional
    public ReviewResponse claimForReview(UUID applicationId, UUID adminId) {
        var application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        var admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Perform transition
        var newStatus = stateMachine.transition(application.getStatus(), ApplicationStateMachine.Event.CLAIM_FOR_REVIEW);
        application.setStatus(newStatus);

        // Create approval history
        var approvalHistory = new ApprovalHistory();
        approvalHistory.setApplication(application);
        approvalHistory.setAdmin(admin);
        approvalHistory.setAction("CLAIMED_FOR_REVIEW");
        approvalHistory.setNotes("Application claimed for review");
        application.addApprovalHistory(approvalHistory);

        application = applicationRepository.save(application);

        return new ReviewResponse(
                application.getId(),
                application.getStatus().name(),
                admin.getId(),
                application.getUpdatedAt()
        );
    }

    @Transactional
    public DecisionResponse makeDecision(UUID applicationId, UUID adminId, DecisionRequest request) {
        var application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        var admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Validate reviewer is the one who claimed the review
        Optional<ApprovalHistory> lastClaim = application.getApprovalHistories().stream()
                .filter(ah -> ah.getAction().equals("CLAIMED_FOR_REVIEW"))
                .reduce((first, second) -> second);

        if (lastClaim.isPresent() && !lastClaim.get().getAdmin().getId().equals(adminId)) {
            throw new ReviewerMismatchException();
        }

        // Perform transition
        var event = request.decision() == DecisionRequest.Decision.APPROVED
                ? ApplicationStateMachine.Event.APPROVE
                : ApplicationStateMachine.Event.REJECT;
        var newStatus = stateMachine.transition(application.getStatus(), event);
        application.setStatus(newStatus);

        // Create approval history
        var approvalHistory = new ApprovalHistory();
        approvalHistory.setApplication(application);
        approvalHistory.setAdmin(admin);
        approvalHistory.setAction(request.decision().name());
        approvalHistory.setNotes(request.notes());
        application.addApprovalHistory(approvalHistory);

        application = applicationRepository.save(application);

        return new DecisionResponse(
                application.getId(),
                application.getStatus().name(),
                admin.getId(),
                request.notes(),
                application.getUpdatedAt()
        );
    }
}
