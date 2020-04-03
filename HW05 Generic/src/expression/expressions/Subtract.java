package expression.expressions;

import expression.types.Type;

public class Subtract<T> extends BinaryExpression<T> {
    public Subtract(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }
    @Override
    public T calc(T a, T b) {
        return operations.subtract(a, b);
    }
}
