package expression.expressions;

import java.util.Objects;

public class Variable implements FullExpression {
    private String variable;

    public Variable(String variable) {
        this.variable = variable;
    }

    public int evaluate(int value) {
        return value;
    }

    public String toString() {
        return variable;
    }

    public void toString(StringBuilder into) {
        into.append(variable);
    }

    public String toMiniString() {
        return variable;
    }

    public void toMiniString(StringBuilder into) {
        into.append(variable);
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Variable var = (Variable) obj;

        return getOperation().equals(var.getOperation());
    }

    public String getOperation() {
        return variable;
    }

    public int hashCode() {
        return Objects.hash(variable);
    }


    public int getPriority() {
        return 3;
    }

    public boolean getImportant() {
        return false;
    }

    public boolean needToCheck() {
        return false;
    }

    public double evaluate(double value) {
        return value;
    }

    public int evaluate(int x, int y, int z) {
        return getTriple(x, y, z);
    }

    private int getTriple(int x, int y, int z) {
        if (variable.equals("x")) {
            return x;
        } else if (variable.equals("y")) {
            return y;
        } else {
            return z;
        }
    }
}
