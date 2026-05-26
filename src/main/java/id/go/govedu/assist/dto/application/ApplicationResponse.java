package id.go.govedu.assist.dto.application;

import id.go.govedu.assist.model.Application;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
    UUID application_id,
    UUID program_id,
    String status,
    LocalDateTime created_at
) {
    public static ApplicationResponse fromEntity(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getProgram().getId(),
                application.getStatus().name(),
                application.getCreatedAt()
        );
    }
}
