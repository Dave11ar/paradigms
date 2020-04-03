package expression.expressions;

import expression.types.Type;

public class Count<T> extends UnaryExpression<T> {
    public Count(TripleExpression<T> expression, Type<T> operations) {
        super(expression, operations);
    }

    @Override
    protected T calc(T a) {
        return operations.count(a);
    }
}
