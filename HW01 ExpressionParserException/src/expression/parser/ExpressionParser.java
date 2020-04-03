package expression.parser;

import expression.expressions.*;
import expression.exceptions.*;

public class ExpressionParser extends BaseParser implements Parser {
    public CommonExpression parse(String expression) {
        super.init(expression);
        CommonExpression res = parseExpression();
        if (test(')')) {
            throw new BracketBalanceException("no opening bracket at: " + actualNumber);
        }
        if (!test('\n')) {
            throw new InputFormatException("no binary expression at: " + actualNumber);
        }

        return res;
    }

    private FullExpression parseExpression() {
        return parseBinary(Parameters.maxLevel);
    }

    private FullExpression parseBinary(int curLevel) {
        if (curLevel == 0) {
            return parseUnary();
        }
        FullExpression res = parseBinary(curLevel - 1);

        while (true) {
            skipWhitespaces();
            String curBinaryExpression = getChar();
            if (sameLevelBinary(curBinaryExpression, curLevel)) {
                nextChar();
                res = makeBinaryExpression(curBinaryExpression,
                        res, parseBinary(curLevel - 1));
            } else {
                return res;
            }
        }
    }

    private FullExpression parseUnary() {
        skipWhitespaces();
        FullExpression res;

        if (test('-')) {
            if (between('0', '9')) {
                res = intParse(true);
            } else {
                try {
                    res = new CheckedNegate(parseUnary());
                } catch (InputFormatException e) {
                    throw new InputFormatException("no argument at: " + actualNumber);
                }
            }
        } else if (between('0', '9')) {
            res = intParse(false);
        } else if (test('(')) {
            res = parseExpression();
            skipWhitespaces();
            if (!test(')')) {
                throw new BracketBalanceException("no closing bracket");
            }
        } else {
            String curVariable = unaryExpressionParse();
            if (Parameters.VARIABLES.contains(curVariable)) {
                res = new Variable(curVariable);
            } else {
                if (getChar().equals(")")) {
                    throw new ArgumentException("no argument at: " + (actualNumber - 1));
                }
                throw new InputFormatException("unknown expression at: " + actualNumber);
            }

        }

        return res;
    }

    private static boolean sameLevelBinary(String expression ,int level) {
        return Parameters.PRIORITIES.containsKey(expression) &&
                Parameters.PRIORITIES.get(expression) == level;
    }

    private FullExpression makeBinaryExpression(String expression, FullExpression a, FullExpression b) {
        switch (expression) {
            case "+":
                return new CheckedAdd(a, b);
            case "-":
                return new CheckedSubtract(a, b);
            case "*":
                return new CheckedMultiply(a, b);
            case "/":
                return new CheckedDivide(a, b);
            default:
                return null;
        }
    }
}
