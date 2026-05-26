package id.go.govedu.assist.dto.bank;

import id.go.govedu.assist.validation.ValidBankCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankAccountRequest(

    @NotBlank(message = "Bank code is required")
    @ValidBankCode(message = "Invalid bank_code. Supported codes: MANDIRI, BRI, BNI, BTN")
    String bank_code,

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^\\d{10,20}$", message = "Account number must be between 10 and 20 digits")
    String account_number,

    @NotBlank(message = "Account name is required")
    @Size(min = 3, max = 100, message = "Account name must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Z\\s]+$", message = "Account name must contain only uppercase letters and spaces")
    String account_name
) {
    public BankAccountRequest {
        if (bank_code == null || bank_code.isBlank()) {
            throw new IllegalArgumentException("Bank code is required");
        }
        if (account_number == null || account_number.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (account_name == null || account_name.isBlank()) {
            throw new IllegalArgumentException("Account name is required");
        }
    }
}
