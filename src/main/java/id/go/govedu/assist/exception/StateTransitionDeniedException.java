package id.go.govedu.assist.exception;

public class StateTransitionDeniedException extends RuntimeException {

    private final String code;
    private final String fromState;
    private final String toState;

    public StateTransitionDeniedException(String fromState, String toState) {
        super("Cannot transition from " + fromState + " to " + toState);
        this.code = "STATE_TRANSITION_DENIED";
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getCode() {
        return code;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }
}
