package id.go.govedu.assist.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name
) {}
