package expression.expressions;

import expression.exceptions.MultiplyOverflowException;

public class CheckedMultiply extends BinaryExpression {
    public CheckedMultiply(FullExpression first, FullExpression second) {
        super(first, second, "*", 2, false);
    }

    public int oper(int a, int b) {
        return check(a, b);
    }

    public int evaluate(int x) {
        return super.evaluate(x);
    }

    public int evaluate(int x, int y, int z) {
        return super.evaluate(x, y, z);
    }

    public static int check(int a, int b) {
        if (a == 0 || b == 0) {
            return a * b;
        }

        if (a == Integer.MIN_VALUE && b == -1 || a == -1 && b == Integer.MIN_VALUE) {
            throw new MultiplyOverflowException("overflow");
        }

        if (a == -1 || b == -1) {
            return a * b;
        }

        if (a < 0 && b < 0) {
            if (Integer.MAX_VALUE / a > b) {
                throw new MultiplyOverflowException("overflow");
            }
        } else if (a > 0 && b < 0) {
            if (Integer.MIN_VALUE / b < a) {
                throw new MultiplyOverflowException("overflow");
            }
        } else if (a < 0 && b > 0){
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
}
