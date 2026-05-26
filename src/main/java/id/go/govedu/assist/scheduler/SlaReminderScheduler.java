package id.go.govedu.assist.scheduler;

import id.go.govedu.assist.kafka.SlaReminderEvent;
import id.go.govedu.assist.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaReminderScheduler {

    private final ApplicationRepository applicationRepository;
    private final KafkaTemplate<String, SlaReminderEvent> kafkaTemplate;

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1 AM
    public void scanSlaBreaches() {
        log.info("Starting SLA breach scan...");

        // Find applications stuck in SUBMITTED or IN_REVIEW for more than 3 days
        var slaThreshold = LocalDateTime.now().minusDays(3);
        var breachedApplications = applicationRepository.findSlaBreachedApplications(slaThreshold);

        if (breachedApplications.isEmpty()) {
            log.info("No SLA breaches found.");
            return;
        }

        log.info("Found {} SLA breaches.", breachedApplications.size());

        // Group by institution code
        Map<String, List<UUID>> groupedByInstitution = new HashMap<>();
        for (var app : breachedApplications) {
            groupedByInstitution
                    .computeIfAbsent(app.getProgram().getInstitutionCode(), k -> new java.util.ArrayList<>())
                    .add(app.getId());
        }

        // Send events to Kafka for each institution
        for (var entry : groupedByInstitution.entrySet()) {
            var event = new SlaReminderEvent(
                    entry.getValue(),
                    entry.getKey(),
                    "Application has been in current status for more than 3 days"
            );
            kafkaTemplate.send("sla-reminders", event);
            log.info("Sent SLA reminder event for institution: {}, application count: {}",
                    entry.getKey(), entry.getValue().size());
        }

        log.info("SLA breach scan completed.");
    }
}
