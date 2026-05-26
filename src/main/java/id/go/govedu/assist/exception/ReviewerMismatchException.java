package id.go.govedu.assist.exception;

public class ReviewerMismatchException extends RuntimeException {

    private final String code = "REVIEWER_MISMATCH";

    public ReviewerMismatchException() {
        super("You cannot decide on this application. It is currently locked by another reviewer.");
    }

    public String getCode() {
        return code;
    }
}
