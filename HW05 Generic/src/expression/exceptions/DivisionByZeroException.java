package expression.exceptions;

public class DivisionByZeroException extends DivideOverflowException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
