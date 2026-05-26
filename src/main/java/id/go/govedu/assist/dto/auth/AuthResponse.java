package id.go.govedu.assist.dto.auth;

public record AuthResponse(
    String access_token,
    String refresh_token,
    Long expires_in,
    String token_type
) {}
