package id.go.govedu.assist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.go.govedu.assist.client.BansosExternalClient;
import id.go.govedu.assist.dto.external.BansosRequest;
import id.go.govedu.assist.dto.external.BansosResponse;
import id.go.govedu.assist.kafka.ApplicationSubmittedEvent;
import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.model.FraudFlag;
import id.go.govedu.assist.repository.ApplicationRepository;
import id.go.govedu.assist.repository.FraudFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoubleFundingCheckerService {

    private final BansosExternalClient bansosClient;
    private final FraudFlagRepository fraudFlagRepository;
    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "application-submitted-topic", groupId = "fraud-checker-group")
    public void handleApplicationSubmitted(ApplicationSubmittedEvent event) {
        try {
            BansosResponse response = bansosClient.checkStatus(new BansosRequest(event.nik()));

            if (response.data().isReceivingAid()) {
                // Create Fraud Flag
                FraudFlag flag = new FraudFlag();
                flag.setApplication(applicationRepository.findById(event.applicationId()).orElseThrow());
                flag.setFlagType(FraudFlag.FlagType.DOUBLE_FUNDING);
                flag.setExternalSource(response.data().activePrograms().get(0).programName());
                flag.setStatus(FraudFlag.FlagStatus.UNRESOLVED);
                flag.setDetails(objectMapper.writeValueAsString(response.data().activePrograms()));
                fraudFlagRepository.save(flag);

                // Suspend application
                var application = applicationRepository.findById(event.applicationId()).orElseThrow();
                application.setStatus(Application.ApplicationStatus.SUSPENDED);
                application.setHasActiveFraudFlag(true);
                applicationRepository.save(application);

                log.warn("Fraud flag created for application: {} due to double funding from: {}",
                        event.applicationId(), response.data().activePrograms().get(0).programName());
            }

        } catch (Exception e) {
            log.error("Failed to check double funding for NIK: {}", event.nik(), e);
        }
    }
}
