package id.go.govedu.assist.dto.application;

import id.go.govedu.assist.model.Application;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DecisionRequest(

    @NotNull(message = "Decision is required")
    Decision decision,

    @NotBlank(message = "Notes are required")
    String notes
) {
    public enum Decision {
        APPROVED,
        REJECTED
    }
}
