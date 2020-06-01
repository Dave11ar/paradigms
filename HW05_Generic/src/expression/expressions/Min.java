package expression.expressions;

import expression.types.Type;

public class Min<T> extends BinaryExpression<T> {
    public Min(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }

    @Override
    protected T calc(T a, T b) {
        return operations.min(a, b);
    }
}
