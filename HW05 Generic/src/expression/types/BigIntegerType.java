package expression.types;

import expression.exceptions.DivisionByZeroException;

import java.math.BigInteger;

public class BigIntegerType implements Type<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException("divide by zero");
        }
        return a.divide(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger count(BigInteger a) {
        return BigInteger.valueOf(a.bitCount());
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        return a.max(b);
    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        return a.min(b);
    }


    @Override
    public BigInteger parse(String source) {
        return new BigInteger(source);
    }
}
