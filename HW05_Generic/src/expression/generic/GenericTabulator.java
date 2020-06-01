package expression.generic;

import expression.expressions.TripleExpression;
import expression.parser.ExpressionParser;
import expression.types.*;
import expression.exceptions.OverflowException;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        Type<? extends Number> operations;

        switch (mode) {
            case "i":
                operations = new IntCheckedType();
                break;
            case "d":
                operations = new DoubleType();
                break;
            case "bi":
                operations = new BigIntegerType();
                break;
            case "u":
                operations = new IntUncheckedType();
                break;
            case "l":
                operations = new LongType();
                break;
            case "s":
                operations = new ShortType();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }

        return makeTable(operations, expression, x1, x2, y1, y2, z1, z2);
    }

    private  <T extends Number> Object[][][] makeTable(Type<T> operations, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        ExpressionParser<T> parser = new ExpressionParser<>(operations);
        TripleExpression<T> parsedExpression = parser.parse(expression);

        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        table[i - x1][j - y1][k - z1] = parsedExpression.evaluate(operations.parse(Integer.toString(i)),
                                operations.parse(Integer.toString(j)), operations.parse(Integer.toString(k)));
                    } catch (OverflowException e) {
                        table[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }

        return table;
    }
}
