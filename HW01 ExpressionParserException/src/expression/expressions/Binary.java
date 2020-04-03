package expression.expressions;

public interface Binary extends FullExpression {
    FullExpression getFirst();
    FullExpression getSecond();
}
