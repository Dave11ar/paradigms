package expression.expressions;

import expression.types.Type;

public class Add<T> extends BinaryExpression<T> {
    public Add(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        super(first, second, operations);
    }
    @Override
    public T calc(T a, T b) {
        return operations.add(a, b);
    }
}
