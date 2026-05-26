package id.go.govedu.assist.exception;

import id.go.govedu.assist.dto.common.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException ex) {
        HttpStatus status = switch (ex.getCode()) {
            case "AUTH_CONFLICT" -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNAUTHORIZED;
        };
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage(), ex.getDetails()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ApiResponse.ValidationErrorDetail> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ApiResponse.ValidationErrorDetail(error.getField(), error.getDefaultMessage()));
        }
        ApiResponse.ValidationErrorDetails details = new ApiResponse.ValidationErrorDetails(
                errors.toArray(new ApiResponse.ValidationErrorDetail[0])
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", "Validation failed", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiResponse.ValidationErrorDetail> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            errors.add(new ApiResponse.ValidationErrorDetail(field, violation.getMessage()));
        }
        ApiResponse.ValidationErrorDetails details = new ApiResponse.ValidationErrorDetails(
                errors.toArray(new ApiResponse.ValidationErrorDetail[0])
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", "Validation failed", details));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("AUTH_INVALID_CREDENTIALS", "Invalid email or password"));
    }

    @ExceptionHandler(KycException.class)
    public ResponseEntity<ApiResponse<Void>> handleKycException(KycException ex) {
        HttpStatus status = switch (ex.getCode()) {
            case "KYC_SYSTEM_ERROR" -> HttpStatus.SERVICE_UNAVAILABLE;
            case "KYC_NIK_MISMATCH", "KYC_DATA_MISMATCH" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(StateTransitionDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleStateTransitionDenied(StateTransitionDeniedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(IncompleteDocumentsException.class)
    public ResponseEntity<ApiResponse<Void>> handleIncompleteDocuments(IncompleteDocumentsException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(ReviewerMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleReviewerMismatch(ReviewerMismatchException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
