package id.go.govedu.assist.dto.delegation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateDelegationRequest(

    @NotBlank(message = "Delegatee NIP is required")
    String delegatee_nip,

    @NotNull(message = "Start date is required")
    LocalDateTime start_date,

    @NotNull(message = "End date is required")
    LocalDateTime end_date,

    @NotBlank(message = "Notes are required")
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    String notes
) {
    public CreateDelegationRequest {
        if (start_date.isAfter(end_date)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
