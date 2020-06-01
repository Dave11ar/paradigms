package expression.expressions;

import expression.types.Type;

public abstract class UnaryExpression<T> implements TripleExpression<T> {
    protected TripleExpression<T> expression;
    protected Type<T> operations;

    public UnaryExpression (TripleExpression<T> expression, Type<T> operations) {
        this.expression = expression;
        this.operations = operations;
    }

    public T evaluate(T x, T y, T z) {
        return calc(expression.evaluate(x, y, z));
    }

    protected abstract T calc(T a);
}
