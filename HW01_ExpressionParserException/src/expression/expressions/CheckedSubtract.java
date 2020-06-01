package expression.expressions;

import expression.exceptions.SubtractOverflowException;

public class CheckedSubtract extends BinaryExpression {
    public CheckedSubtract(FullExpression first, FullExpression second) {
        super(first, second, "-", 1, true);
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

    private int check(int a, int b) {
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
}
