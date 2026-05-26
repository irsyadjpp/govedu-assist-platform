package id.go.govedu.assist.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private UUID id;
    private String nik;
    private String name;
    private String email;
    private Boolean ekyc_verified;
    private BankAccountInfo active_bank_account;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BankAccountInfo {
        private UUID id;
        private String bank_code;
        private String account_number;
        private String account_name;
    }
}
