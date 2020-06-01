package expression.parser;

import expression.exceptions.*;
import expression.expressions.*;
import expression.types.Type;

public class ExpressionParser<T extends Number> extends BaseParser implements Parser<T> {
    private Type<T> operations;
    String curBinaryExpression = "";

    public ExpressionParser(Type<T> operations) {
        this.operations = operations;
    }

    public TripleExpression<T> parse(String expression) {
        super.init(expression);
        TripleExpression<T> res = parseExpression();
        if (test(')')) {
            throw new BracketBalanceException("no opening bracket at: " + actualNumber);
        }
        if (!test('\n')) {
            throw new InputFormatException("no binary expression at: " + actualNumber);
        }

        return res;
    }

    private TripleExpression<T> parseExpression() {
        return parseBinary(Parameters.maxLevel);
    }

    private TripleExpression<T> parseBinary(int curLevel) {
        if (curLevel == 0) {
            return parseUnary();
        }
        TripleExpression<T> res = parseBinary(curLevel - 1);

        while (true) {
            skipWhitespaces();
            if (curBinaryExpression.equals("")) {
                curBinaryExpression = expressionParse();
            }

            if (sameLevelBinary(curBinaryExpression, curLevel)) {
                String tmp = curBinaryExpression;
                curBinaryExpression = "";

                res = makeBinaryExpression(tmp,
                        res, parseBinary(curLevel - 1));
            } else {
                return res;
            }
        }
    }

    private TripleExpression<T> parseUnary() {
        skipWhitespaces();
        TripleExpression<T> res;

        String curUnaryExpression = expressionParse();

        if (curUnaryExpression.equals("count")) {
            res = new Count<T>(parseUnary(), operations);
        } else if (curUnaryExpression.equals("-")) {
            if (between('0', '9')) {
                res = intParse(curUnaryExpression + expressionParse());
            } else {
                try {
                    res = new Negate<T>(parseUnary(), operations);
                } catch (InputFormatException e) {
                    throw new InputFormatException("no argument at: " + actualNumber);
                }
            }
        } else if (!curUnaryExpression.isEmpty() &&
                Character.isDigit(curUnaryExpression.charAt(0))) {
            res = intParse(curUnaryExpression);
        } else if (curUnaryExpression.equals("(")) {
            res = parseExpression();
            skipWhitespaces();
            if (!test(')')) {
                throw new BracketBalanceException("no closing bracket at:" + actualNumber);
            }
        } else {
            if (Parameters.VARIABLES.contains(curUnaryExpression)) {
                res = new Variable<T>(curUnaryExpression);
            } else {
                if (String.valueOf(getChar()).equals(")")) {
                    throw new ArgumentException("no argument at: " + actualNumber);
                }
                throw new InputFormatException("unknown expression at: " + actualNumber);
            }

        }

        return res;
    }

    private boolean sameLevelBinary(String expression, int level) {
        return Parameters.PRIORITIES.containsKey(expression) &&
                Parameters.PRIORITIES.get(expression) == level;
    }

    private TripleExpression<T> makeBinaryExpression(String expression, TripleExpression<T> a, TripleExpression<T> b) {
        switch (expression) {
            case "+":
                return new Add<T>(a, b, operations);
            case "-":
                return new Subtract<T>(a, b, operations);
            case "*":
                return new Multiply<T>(a, b, operations);
            case "/":
                return new Divide<T>(a, b, operations);
            case "min":
                return new Min<T>(a, b, operations);
            case "max":
                return new Max<T>(a, b, operations);
            default:
                return null;
        }
    }

    protected TripleExpression<T> intParse(String number) {
        try {
            return new Const<T>(operations.parse(number));
        } catch (NumberFormatException e) {
            throw new ConstOverflow("const overflow at: " + actualNumber);
        }
    }
}
