package expression.tests;

import expression.expressions.TripleExpression;
import expression.parser.ExpressionParser;

public class Test {
    public static void main(String[] args) {
        ExpressionParser e = new ExpressionParser();
        TripleExpression t = e.parse("-(-(-\t\t-5 + 16   *x*y) + 1 * z) -(((-11)))");
      int p = t.evaluate(0, 0, -10);
//
//        p = p;
    }
}
