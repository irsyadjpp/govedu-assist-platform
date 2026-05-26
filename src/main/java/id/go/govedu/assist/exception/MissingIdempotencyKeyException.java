package id.go.govedu.assist.exception;

public class MissingIdempotencyKeyException extends RuntimeException {

    private final String code = "IDEMPOTENCY_KEY_MISSING";

    public MissingIdempotencyKeyException() {
        super("Idempotency-Key header is required.");
    }

    public String getCode() {
        return code;
    }
}
