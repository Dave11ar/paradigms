package expression.expressions;

import expression.types.Type;

public class Multiply<T> extends BinaryExpression<T> {
    public Multiply(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }

    @Override
    public T calc(T a, T b) {
        return operations.multiply(a, b);
    }
}
