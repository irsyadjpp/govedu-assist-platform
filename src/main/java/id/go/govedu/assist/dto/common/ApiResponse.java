package id.go.govedu.assist.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;
    private String code;
    private ValidationErrorDetails details;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data, null, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>("error", message, null, code, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, ValidationErrorDetails details) {
        return new ApiResponse<>("error", message, null, code, details);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationErrorDetails {
        private ValidationErrorDetail[] errors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDetail {
        private String field;
        private String error;
    }
}
