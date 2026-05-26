package id.go.govedu.assist.kafka;

import id.go.govedu.assist.model.Admin;
import id.go.govedu.assist.model.Delegation;
import id.go.govedu.assist.repository.AdminRepository;
import id.go.govedu.assist.repository.DelegationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaReminderConsumer {

    private final AdminRepository adminRepository;
    private final DelegationRepository delegationRepository;

    @KafkaListener(topics = "sla-reminders", groupId = "govedu-consumer-group")
    public void processSlaReminder(SlaReminderEvent event) {
        log.info("Processing SLA reminder for institution: {}, application count: {}",
                event.institutionCode(), event.applicationIds().size());

        // Find admin responsible for this institution
        var admin = adminRepository.findByInstitutionCode(event.institutionCode())
                .stream()
                .findFirst()
                .orElse(null);

        if (admin == null) {
            log.warn("No admin found for institution: {}", event.institutionCode());
            return;
        }

        // Check if admin has active delegation
        Optional<Delegation> activeDelegation = delegationRepository.findActiveDelegationForAdmin(
                admin.getId(),
                LocalDateTime.now()
        );

        String recipientName = admin.getName();
        String recipientEmail = admin.getEmail();

        if (activeDelegation.isPresent()) {
            var delegatee = activeDelegation.get().getDelegatee();
            recipientName = delegatee.getName();
            recipientEmail = delegatee.getEmail();
            log.info("Admin {} has active delegation to {}, sending notification to delegatee",
                    admin.getName(), delegatee.getName());
        }

        // Send notification (in real implementation, this would use email service)
        log.info("SLA Reminder Notification:");
        log.info("  To: {} ({})", recipientName, recipientEmail);
        log.info("  Institution: {}", event.institutionCode());
        log.info("  Reason: {}", event.slaBreachReason());
        log.info("  Application IDs: {}", event.applicationIds());
        log.info("  Action Required: Please review and process these applications immediately.");
    }
}
