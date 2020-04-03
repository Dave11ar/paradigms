package expression.types;

public class DoubleType implements Type<Double> {
    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double count(Double a) {
        return (double) Long.bitCount(Double.doubleToLongBits(a));
    }

    @Override
    public Double max(Double a, Double b) {
        return Math.max(a, b);
    }

    @Override
    public Double min(Double a, Double b) {
        return Math.min(a, b);
    }

    @Override
    public Double parse(String source) {
        return Double.parseDouble(source);
    }
}
