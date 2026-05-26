package id.go.govedu.assist.dto.ekyc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EkycVerifyRequest(

    @NotBlank(message = "NIK is required")
    @Pattern(regexp = "^\\d{16}$", message = "NIK must be exactly 16 digits")
    String nik,

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    String full_name,

    @NotBlank(message = "Birth place is required")
    @Size(min = 3, max = 50, message = "Birth place must be between 3 and 50 characters")
    String birth_place,

    @NotBlank(message = "Birth date is required")
    String birth_date
) {
    public EkycVerifyRequest {
        if (nik == null || nik.isBlank()) {
            throw new IllegalArgumentException("NIK is required");
        }
        if (full_name == null || full_name.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (birth_place == null || birth_place.isBlank()) {
            throw new IllegalArgumentException("Birth place is required");
        }
        if (birth_date == null || birth_date.isBlank()) {
            throw new IllegalArgumentException("Birth date is required");
        }
        try {
            LocalDate.parse(birth_date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Birth date must be in ISO format (YYYY-MM-DD)");
        }
    }
}
