package id.go.govedu.assist.dto.admin;

public record AdminLoginResponse(
    String access_token,
    Long expires_in,
    AdminProfile admin_profile
) {}
