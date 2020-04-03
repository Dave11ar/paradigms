package expression.expressions;

public class Const<T> implements TripleExpression<T> {
    private T number;

    public Const(T number) {
        this.number = number;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return number;
    }
}
