package id.go.govedu.assist.exception;

public class KycException extends RuntimeException {

    private final String code;

    public KycException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
