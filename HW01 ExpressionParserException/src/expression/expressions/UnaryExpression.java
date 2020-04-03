package expression.expressions;

import java.util.Objects;

public abstract class UnaryExpression implements Unary {
    private String operation;
    private int priority;
    private boolean important;
    protected FullExpression expression;

    public UnaryExpression (FullExpression expression, String operation, int priority, boolean important) {
        this.expression = expression;
        this.operation = operation;
        this.priority = priority;
        this.important = important;
    }

    @Override
    public String toString() {
        StringBuilder into = new StringBuilder();
        toString(into);
        return into.toString();
    }

    public void toString(StringBuilder into) {
        into.append('(').append(operation).append(" ");
        expression.toString(into);
        into.append(')');
    }

    public String toMiniString() {
        StringBuilder into = new StringBuilder();
        toMiniString(into);
        return into.toString();
    }

    public void toMiniString(StringBuilder into) {
        into.append(operation).append(" ");
        bracket(into, expression);
    }

    private void bracket(StringBuilder into, FullExpression current) {
        boolean bracket = getPriority() > current.getPriority();

        if (bracket) {
            into.append('(');
            current.toMiniString(into);
            into.append(')');
        } else {
            current.toMiniString(into);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        UnaryExpression expression = (UnaryExpression) obj;

        return getOperation().equals(expression.getOperation())
                && this.expression.equals(expression.getExpression());

    }

    public String getOperation() {
        return operation;
    }

    public FullExpression getExpression() {
        return expression;
    }

    public int getPriority() {
        return priority;
    }

    public boolean getImportant() {
        return important;
    }

    public int evaluate(int x) {
        return oper(expression.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return oper(expression.evaluate(x, y, z));
    }

    protected abstract int oper(int a);

    @Override
    public int hashCode() {
        return Objects.hash(expression, operation, priority, important);
    }
}
