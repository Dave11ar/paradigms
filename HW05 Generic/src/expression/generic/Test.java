package expression.generic;

import expression.expressions.TripleExpression;
import expression.parser.ExpressionParser;
import expression.types.IntUncheckedType;
import expression.types.Type;

public class Test {
    public static void main(String[] args) {

        Type<Integer> type = new IntUncheckedType();
        ExpressionParser<Integer> jopa = new ExpressionParser<>(type);

        TripleExpression<Integer> parsed = jopa.parse("x * y + (z - 1) / 10");
    }
}
