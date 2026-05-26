package id.go.govedu.assist.dto.bulk;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BulkApprovalRequest(

    @NotEmpty(message = "Application IDs cannot be empty")
    List<UUID> application_ids,

    @NotBlank(message = "Notes are required")
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    String notes
) {}
