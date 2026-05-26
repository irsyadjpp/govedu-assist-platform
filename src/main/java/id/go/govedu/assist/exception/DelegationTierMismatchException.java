package id.go.govedu.assist.exception;

public class DelegationTierMismatchException extends RuntimeException {

    private final String code = "DELEGATION_TIER_MISMATCH";

    public DelegationTierMismatchException() {
        super("Delegatee must have the same role tier as the delegator.");
    }

    public String getCode() {
        return code;
    }
}
