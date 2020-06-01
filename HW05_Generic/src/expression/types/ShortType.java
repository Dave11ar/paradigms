package expression.types;

import expression.exceptions.DivisionByZeroException;

public class ShortType implements Type<Short> {
    @Override
    public Short add(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short divide(Short a, Short b) {
        if (b == 0) {
            throw new DivisionByZeroException("division by zero");
        }
        return (short) (a / b);
    }

    @Override
    public Short negate(Short a) {
        return (short) -a;
    }

    @Override
    public Short count(Short a) {
        return (short) Integer.bitCount(a & 0xffff);
    }

    @Override
    public Short max(Short a, Short b) {
        return (short) Math.max(a, b);
    }

    @Override
    public Short min(Short a, Short b) {
        return (short) Math.min(a, b);
    }

    @Override
    public Short parse(String source) {
        return (short) Integer.parseInt(source);
    }
}
