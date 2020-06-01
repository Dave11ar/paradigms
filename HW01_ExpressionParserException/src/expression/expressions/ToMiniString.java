package expression.expressions;

public interface ToMiniString {
    default String toMiniString() {
        return toString();
    }
}