package id.go.govedu.assist.dto.bank;

import id.go.govedu.assist.validation.ValidBankCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {

    @NotBlank(message = "Bank code is required")
    @ValidBankCode(message = "Invalid bank_code. Supported codes: MANDIRI, BRI, BNI, BTN")
    private String bank_code;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^\\d{10,20}$", message = "Account number must be between 10 and 20 digits")
    private String account_number;

    @NotBlank(message = "Account name is required")
    @Size(min = 3, max = 100, message = "Account name must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Z\\s]+$", message = "Account name must contain only uppercase letters and spaces")
    private String account_name;
}
