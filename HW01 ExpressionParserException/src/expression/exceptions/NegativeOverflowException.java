package expression.exceptions;

public class NegativeOverflowException extends OverflowException {
    public NegativeOverflowException(String message) {
        super(message);
    }
}
