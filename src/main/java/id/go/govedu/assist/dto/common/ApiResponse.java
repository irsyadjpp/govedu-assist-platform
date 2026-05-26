package id.go.govedu.assist.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    String status,
    String message,
    T data,
    String code,
    ValidationErrorDetails details
) {
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ValidationErrorDetails(ValidationErrorDetail[] errors) {}

    public record ValidationErrorDetail(String field, String error) {}
}
