package expression.parser;

import expression.expressions.Const;
import expression.expressions.FullExpression;
import expression.exceptions.ConstOverflow;

public class BaseParser {
    private StringSource s;
    private char ch;
    protected int actualNumber;

    protected void init(String s) {
        actualNumber = -1;
        this.s = new StringSource(skipStartEndWhitespaces(s));
        nextChar();
    }

    protected String getChar() {
        return String.valueOf(ch);
    }

    protected void nextChar() {
        ch = s.hasNext() ? s.nextChar() : '\n';
        actualNumber++;
    }

    private boolean valid() {
        return ch != '\n';
    }

    protected boolean test(char curChar) {
        if (ch == curChar) {
            nextChar();
            return true;
        }
        return false;
    }

    protected boolean between(char a, char b) {
        return a <= ch && ch <= b;
    }

    protected String unaryExpressionParse() {
        StringBuilder curOperator = new StringBuilder();

        if (!valid()) return "\n";

        while (between('0', '9') || between('A', 'z')) {
            curOperator.append(ch);
            nextChar();
        }

        return curOperator.toString();
    }

    protected FullExpression intParse(boolean isMinus) {
        skipWhitespaces();
        StringBuilder tmp = new StringBuilder(isMinus ? "-" : "");
        while (Character.isDigit(ch)) {
            tmp.append(ch);
            nextChar();
        }

        try {
            return new Const(Integer.parseInt(tmp.toString()));
        } catch (NumberFormatException e) {
            throw new ConstOverflow("const overflow at: " + actualNumber);
        }
    }

    protected void skipWhitespaces() {
        while (valid() && Character.isWhitespace(ch)) {
            nextChar();
        }
    }

    private String skipStartEndWhitespaces(String s) {
        actualNumber++;
        int end = s.length() - 1;
        while(actualNumber < s.length() && Character.isWhitespace(s.charAt(actualNumber))) {
            actualNumber++;
        }
        while(end >= 0 && Character.isWhitespace(s.charAt(end))) {
            end--;
        }
        end++;

        return s.substring(actualNumber, end);
    }
}
