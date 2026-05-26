package id.go.govedu.assist.audit;

import id.go.govedu.assist.model.Application;
import id.go.govedu.assist.service.AuditEventPublisher;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ApplicationAuditListener {

    @Autowired
    private AuditEventPublisher auditEventPublisher;

    @PostPersist
    public void onPostPersist(Application application) {
        Map<String, Object> payload = toMap(application);
        auditEventPublisher.publishEvent("APPLICATION", application.getId(), "CREATE", null, payload);
    }

    @PostUpdate
    public void onPostUpdate(Application application) {
        Map<String, Object> payload = toMap(application);
        auditEventPublisher.publishEvent("APPLICATION", application.getId(), "UPDATE", null, payload);
    }

    @PreRemove
    public void onPreRemove(Application application) {
        Map<String, Object> payload = toMap(application);
        auditEventPublisher.publishEvent("APPLICATION", application.getId(), "DELETE", payload, null);
    }

    private Map<String, Object> toMap(Application application) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", application.getId());
        map.put("status", application.getStatus());
        map.put("hasActiveFraudFlag", application.getHasActiveFraudFlag());
        map.put("createdAt", application.getCreatedAt());
        map.put("updatedAt", application.getUpdatedAt());
        return map;
    }
}
