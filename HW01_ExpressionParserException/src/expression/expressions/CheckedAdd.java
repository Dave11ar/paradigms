package expression.expressions;

import expression.exceptions.AddOverflowException;

public class CheckedAdd extends BinaryExpression {
    public CheckedAdd(FullExpression first, FullExpression second) {
        super(first, second, "+", 1, false);
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
}
