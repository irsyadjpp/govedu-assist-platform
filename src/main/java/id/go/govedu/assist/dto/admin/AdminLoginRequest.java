package id.go.govedu.assist.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(

    @NotBlank(message = "NIP is required")
    String nip,

    @NotBlank(message = "Password is required")
    String password
) {}
