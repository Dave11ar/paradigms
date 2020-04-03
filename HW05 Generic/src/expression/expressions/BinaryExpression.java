package expression.expressions;

import expression.types.*;

public abstract class BinaryExpression<T> implements TripleExpression<T> {
    protected TripleExpression<T> first;
    protected TripleExpression<T> second;
    protected Type<T> operations;

    public BinaryExpression(TripleExpression<T> first, TripleExpression<T> second, Type<T> operations) {
        this.first = first;
        this.second = second;
        this.operations = operations;
    }

    public T evaluate(T x, T y, T z) {
        return calc(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    protected abstract T calc(T a, T b);
}
