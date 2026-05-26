package id.go.govedu.assist.exception;

import id.go.govedu.assist.dto.common.ApiResponse;

public class ValidationException extends RuntimeException {

    private final String code;
    private final ApiResponse.ValidationErrorDetails details;

    public ValidationException(String message) {
        super(message);
        this.code = "VALIDATION_ERROR";
        this.details = null;
    }

    public ValidationException(String code, String message, ApiResponse.ValidationErrorDetails details) {
        super(message);
        this.code = code;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public ApiResponse.ValidationErrorDetails getDetails() {
        return details;
    }
}
