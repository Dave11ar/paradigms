package expression.expressions;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface Expression extends ToMiniString {
    int evaluate(int x);
}