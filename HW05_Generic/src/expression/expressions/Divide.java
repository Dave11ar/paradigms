package expression.expressions;

import expression.types.Type;

public class Divide<T> extends BinaryExpression<T> {
    public Divide(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }

    @Override
    public T calc(T a, T b) {
        return operations.divide(a, b);
    }
}
