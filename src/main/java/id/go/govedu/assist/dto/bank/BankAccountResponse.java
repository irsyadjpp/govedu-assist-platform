package id.go.govedu.assist.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {

    private UUID id;
    private String bank_code;
    private String account_number;
    private String account_name;
    private Boolean is_active;
}
