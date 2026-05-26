package id.go.govedu.assist.exception;

public class IdempotencyMismatchException extends RuntimeException {

    private final String code = "IDEMPOTENCY_MISMATCH";

    public IdempotencyMismatchException() {
        super("Idempotency-Key is reused with a different request payload.");
    }

    public String getCode() {
        return code;
    }
}
