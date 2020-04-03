package expression.parser;

public class BaseParser {
    private StringSource s;
    private char ch;
    protected int actualNumber;

    protected void init(String s) {
        actualNumber = -1;
        this.s = new StringSource(skipStartEndWhitespaces(s));
        nextChar();
    }

    protected char getChar() {
        return ch;
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

    private void operatorParse(StringBuilder curExpression) {
        while (Parameters.OPERATORS.contains(String.valueOf(getChar()))) {
            curExpression.append(ch);
            nextChar();
        }
    }

    private void functionParse(StringBuilder curExpression) {
        while (between('A', 'z')) {
            curExpression.append(ch);
            nextChar();
        }
    }

    private void numberParse(StringBuilder curExpression) {
        while (between('0', '9')) {
            curExpression.append(ch);
            nextChar();
        }
    }

    protected String expressionParse() {
        skipWhitespaces();
        StringBuilder curExpression = new StringBuilder();

        if (!valid()) return "\n";

        operatorParse(curExpression);

        if (curExpression.length() != 0) {
            return curExpression.toString();
        }

        functionParse(curExpression);

        if (curExpression.length() != 0) {
            return curExpression.toString();
        }

        numberParse(curExpression);

        return curExpression.toString();
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
