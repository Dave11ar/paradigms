package expression.expressions;

import java.util.Objects;

public abstract class BinaryExpression implements Binary {
    private String operation;
    private int priority;
    private boolean important;
    protected FullExpression first;
    protected FullExpression second;

    public BinaryExpression(FullExpression first, FullExpression second, String operation, int priority, boolean important) {
        this.first = first;
        this.second = second;
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
        into.append('(');
        first.toString(into);
        into.append(" ").append(operation).append(" ");
        second.toString(into);
        into.append(')');
    }

    public String toMiniString() {
        StringBuilder into = new StringBuilder();
        toMiniString(into);
        return into.toString();
    }

    public void toMiniString(StringBuilder into) {
        bracket(into, true, first);
        into.append(" ").append(operation).append(" ");
        bracket(into, false, second);
    }

    private void bracket(StringBuilder into, boolean firstExp, FullExpression current) {
        boolean bracket = getPriority() > current.getPriority()
                || ((getPriority() == current.getPriority()) && !firstExp
                && (getImportant() || current.getImportant()));

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

        BinaryExpression expression = (BinaryExpression) obj;

        return getOperation().equals(expression.getOperation())
                && first.equals(expression.getFirst())  && second.equals(expression.getSecond());

    }

    public String getOperation() {
        return operation;
    }

    public FullExpression getFirst() {
        return first;
    }

    public FullExpression getSecond() {
        return second;
    }

    public int getPriority() {
        return priority;
    }

    public boolean getImportant() {
        return important;
    }

    public int evaluate(int x) {
        return oper(first.evaluate(x), second.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return oper(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    protected abstract int oper(int a, int b);

    @Override
    public int hashCode() {
        return Objects.hash(first, second, operation, priority, important);
    }
}
