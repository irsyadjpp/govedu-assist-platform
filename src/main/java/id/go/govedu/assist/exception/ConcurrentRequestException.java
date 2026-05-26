package id.go.govedu.assist.exception;

public class ConcurrentRequestException extends RuntimeException {

    private final String code = "IDEMPOTENCY_LOCKED";

    public ConcurrentRequestException() {
        super("A request with this Idempotency-Key is currently being processed. Please wait and try again.");
    }

    public String getCode() {
        return code;
    }
}
