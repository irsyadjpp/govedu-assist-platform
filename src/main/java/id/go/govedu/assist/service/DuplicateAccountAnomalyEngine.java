package id.go.govedu.assist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.go.govedu.assist.kafka.BankAccountRegisteredEvent;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.FraudFlag;
import id.go.govedu.assist.repository.ApplicationRepository;
import id.go.govedu.assist.repository.BankAccountRepository;
import id.go.govedu.assist.repository.FraudFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuplicateAccountAnomalyEngine {

    private final BankAccountRepository bankAccountRepository;
    private final FraudFlagRepository fraudFlagRepository;
    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "bank-account-registered-topic", groupId = "fraud-anomaly-group")
    @Transactional
    public void evaluateDuplicateBank(BankAccountRegisteredEvent event) {
        List<UUID> linkedUserIds = bankAccountRepository.findUserIdsByAccountNumberAndBankCode(
                event.accountNumber(),
                event.bankCode()
        );

        if (linkedUserIds.size() > 1) {
            log.warn("Duplicate bank account detected: {} {} used by {} users",
                    event.bankCode(), event.accountNumber(), linkedUserIds.size());

            for (UUID userId : linkedUserIds) {
                Application app = applicationRepository.findActiveApplicationByUserId(userId).orElse(null);

                if (app != null && !app.getStatus().equals(Application.ApplicationStatus.SUSPENDED)) {
                    FraudFlag flag = new FraudFlag();
                    flag.setApplication(app);
                    flag.setFlagType(FraudFlag.FlagType.DUPLICATE_BANK);
                    flag.setExternalSource(event.bankCode());
                    flag.setStatus(FraudFlag.FlagStatus.UNRESOLVED);

                    Map<String, Object> details = new HashMap<>();
                    details.put("accountNumber", event.accountNumber());
                    details.put("bankCode", event.bankCode());
                    details.put("linkedUserIds", linkedUserIds);

                    try {
                        flag.setDetails(objectMapper.writeValueAsString(details));
                    } catch (Exception e) {
                        log.error("Failed to serialize fraud flag details", e);
                    }

                    fraudFlagRepository.save(flag);

                    app.setStatus(Application.ApplicationStatus.SUSPENDED);
                    app.setHasActiveFraudFlag(true);
                    applicationRepository.save(app);

                    log.info("Suspended application {} due to duplicate bank account", app.getId());
                }
            }
        }
    }
}
