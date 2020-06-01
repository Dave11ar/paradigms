package expression.expressions;

import expression.exceptions.DivideOverflowException;
import expression.exceptions.DivisionByZeroException;

public class CheckedDivide extends BinaryExpression {
    public CheckedDivide(FullExpression first, FullExpression second) {
        super(first, second, "/", 2, true);
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
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new DivideOverflowException("overflow");
        } else if (b == 0) {
            throw new DivisionByZeroException("division by zero");
        }

        return a / b;
    }
}
