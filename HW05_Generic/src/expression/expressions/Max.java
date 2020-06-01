package expression.expressions;

import expression.types.Type;

public class Max<T extends Number> extends BinaryExpression<T> {
    public Max(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }

    @Override
    protected T calc(T a, T b) {
        return operations.max(a, b);
    }
}
