package id.go.govedu.assist.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String access_token;
    private String refresh_token;
    private Long expires_in;
    private String token_type;
}
