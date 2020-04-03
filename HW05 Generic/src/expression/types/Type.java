package expression.types;

public interface Type<T> {
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T negate(T a);
    T count(T a);
    T max(T a, T b);
    T min(T a, T b);
    T parse(String source);
}
