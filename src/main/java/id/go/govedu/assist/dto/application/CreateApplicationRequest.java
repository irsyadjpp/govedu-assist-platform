package id.go.govedu.assist.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateApplicationRequest(

    @NotNull(message = "Program ID is required")
    UUID program_id
) {}
