package expression.expressions;

import expression.types.Type;

public class Negate<T> extends UnaryExpression<T> {
    public Negate(TripleExpression<T> expression, Type<T> operations) {
        super(expression, operations);
    }

    @Override
    protected T calc(T a) {
        return operations.negate(a);
    }
}
