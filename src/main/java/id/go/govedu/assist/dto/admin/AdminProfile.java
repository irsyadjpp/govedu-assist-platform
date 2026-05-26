package id.go.govedu.assist.dto.admin;

import java.util.UUID;

public record AdminProfile(
    UUID id,
    String name,
    String role_tier,
    String institution_code
) {}
