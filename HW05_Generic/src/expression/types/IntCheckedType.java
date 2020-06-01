package expression.types;

import expression.exceptions.*;

public class IntCheckedType implements Type<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        if (a > 0 && b > 0) {
            if (Integer.MAX_VALUE - a < b) {
                throw new AddOverflowException("overflow");
            }
        } else if (a < 0 && b < 0) {
            if (Integer.MIN_VALUE - a > b) {
                throw new AddOverflowException("overflow");
            }
        }

        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (a == 0 & b == Integer.MIN_VALUE) {
            throw new SubtractOverflowException("overflow");
        } else if (a > 0 && b < 0) {
            if (-(Integer.MAX_VALUE - a) > b) {
                throw new SubtractOverflowException("overflow");
            }
        } else if (a < 0 && b > 0) {
            if (-(Integer.MIN_VALUE - a) < b) {
                throw new SubtractOverflowException("overflow");
            }
        }

        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (a == Integer.MIN_VALUE && b == -1) {
            throw new MultiplyOverflowException("overflow");
        }

        if (a == 0 || a == -1) {
            return a * b;
        }

        if (b < 0) {
            if (Integer.MAX_VALUE / a > b) {
                throw new MultiplyOverflowException("overflow");
            }
        } else if (a < 0){
            if (Integer.MIN_VALUE / a < b) {
                throw new MultiplyOverflowException("overflow");
            }
        } else {
            if (Integer.MAX_VALUE / a < b) {
                throw new MultiplyOverflowException("overflow");
            }
        }

        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new DivideOverflowException("overflow");
        } else if (b == 0) {
            throw new DivisionByZeroException("division by zero");
        }

        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new NegativeOverflowException("overflow");
        }

        return -a;
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @Override
    public Integer parse(String source) {
        return Integer.parseInt(source);
    }
}
