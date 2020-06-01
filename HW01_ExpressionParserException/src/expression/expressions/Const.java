package expression.expressions;

import java.util.Objects;

public class Const implements FullExpression {
    private Integer number;

    public Const(Integer number) {
        this.number = number;
    }

    public int evaluate(int value) {
        return number;
    }

    public int evaluate(int x, int y, int z) {
        return number;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public void toString(StringBuilder into) {
        into.append(number.toString());
    }

    @Override
    public String toMiniString() {
        return number.toString();
    }

    @Override
    public void toMiniString(StringBuilder into) {
        into.append(number.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Const con = (Const) obj;
        return getOperation().equals(con.getOperation());
    }

    @Override
    public String getOperation() {
        return number.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    public int getPriority() {
        return 3;
    }

    public boolean getImportant() {
        return false;
    }
}
