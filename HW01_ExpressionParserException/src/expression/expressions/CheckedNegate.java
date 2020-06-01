package expression.expressions;

import expression.exceptions.NegativeOverflowException;

public class CheckedNegate extends UnaryExpression {
    public CheckedNegate(FullExpression expression) {
        super(expression, "-", 3, false);
    }

    public int oper(int a) {
        return check(a);
    }

    public int evaluate(int x) {
        return super.evaluate(x);
    }

    public int evaluate(int x, int y, int z) {
        return super.evaluate(x, y, z);
    }
    private int check(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new NegativeOverflowException("overflow");
        }

        return -a;
    }
}
