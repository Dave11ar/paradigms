"use strict";

const VARIABLES =['x', 'y', 'z'];

const abstractFunction = {
    toString : function() {
        return this.terms.map((a) => a.toString()).reduce(
            (a, b) => a + ' ' + b)  + ' ' + this.sign
    },
    prefix : function() {
        return '(' + this.sign  + (this.terms.length === 0 ?
            ' ' : this.terms.map((a) => a.prefix()).reverse().reduceRight((a, b) => a + ' ' + b , '')) + ')'
    },
    postfix : function() {
        return '(' + (this.terms.length !== 0 ? this.terms.map((a) => a.postfix()).reverse().reduceRight(
            (a, b) => a + ' ' + b) : '') + ' ' + this.sign + ')'
    },
    evaluate : function (...args) { return this.operation(...this.terms.map((a) => a.evaluate(...args))) }
};

function OperationFactory(sign, operation, diff) {
    const protoOperation = {
        sign : sign,
        operation : operation,
        diff : diff
    }
    Object.setPrototypeOf(protoOperation, abstractFunction);

    return protoOperation;
}

Add.prototype = OperationFactory(
    '+',
    (a, b) => a + b,
    function(variable) { return  new Add(...this.terms.map((a) => a.diff(variable))) }
);

Subtract.prototype = OperationFactory(
    '-',
    (a, b) => a - b,
    function(variable) { return new Subtract(...this.terms.map((a) => a.diff(variable))) }
);
Multiply.prototype = OperationFactory(
    '*',
    (a, b) => a * b,
    function(variable) { return  new Add(new Multiply(this.terms[0].diff(variable), this.terms[1]),
            new Multiply(this.terms[0], this.terms[1].diff(variable))) }
);
Divide.prototype = OperationFactory(
    '/',
    (a, b) => a / b,
    function(variable) { return new Divide(new Subtract(new Multiply(this.terms[0].diff(variable), this.terms[1]),
            new Multiply(this.terms[0], this.terms[1].diff(variable))), new Power(this.terms[1], TWO)) }
);
Negate.prototype = OperationFactory(
    'negate',
    (a) => -a,
    function(variable) { return new Negate(this.terms[0].diff(variable)) }
);
Log.prototype = OperationFactory(
    'log',
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    function(variable) { return new Divide(new Subtract(new Multiply(new Multiply(new Divide(ONE, this.terms[1]),
            this.terms[1].diff(variable)), new Log(E, this.terms[0])), new Multiply(new Multiply(
            new Divide(ONE, this.terms[0]), this.terms[0].diff(variable)), new Log(E, this.terms[1]))),
            new Power(new Log(E, this.terms[0]), TWO)) }
);
Power.prototype = OperationFactory(
    'pow',
    (a, b) => Math.pow(a, b),
    function(variable) { return new Add(new Multiply(new Multiply(this.terms[1], new Power(this.terms[0],
        new Subtract(this.terms[1], ONE))), this.terms[0].diff(variable)), new Multiply(new Multiply(
        new Power(this.terms[0], this.terms[1]), new Log(E, this.terms[0])), this.terms[1].diff(variable))) }
);
Sumexp.prototype = OperationFactory(
    'sumexp',
    (...terms) => terms.map((a) => Math.pow(Math.E, a)).reduce((a, b) => a + b, 0),
    function(variable) {
        if (this.terms.length === 0) return ZERO;
        let sum = new Power(E, this.terms[0]);
        for (let i = 1; i < this.terms.length; i++) {
            sum = new Add(sum, new Power(E, this.terms[i]));
        }
        return sum.diff(variable);
    }
);
Softmax.prototype = OperationFactory(
    'softmax',
    (...terms) =>  Math.pow(Math.E, terms[0]) / terms.map((a) => Math.pow(Math.E, a)).reduce(
        (a, b) => a + b, 0),
    function(variable) {
        if (this.terms.length === 0) return ZERO;
        let first = new Power(E, this.terms[0]);
        return new Divide(first, new Sumexp(...this.terms)).diff(variable);
    }
);
const terminal = {
    evaluate : function(...vars) { return isNaN(+this.value) ? vars[VARIABLES.indexOf(this.value)] : this.value },
    toString : function() { return this.value.toString() },
    prefix : function() { return this.value.toString() },
    postfix : function() { return this.value.toString() },
    diff : function (...variable) { return (variable.length !== 0 && variable[0] === this.value) ? ONE : ZERO}
}
Const.prototype = terminal
Variable.prototype = terminal

function Add(...terms) { this.terms = terms }
function Subtract(...terms) { this.terms = terms }
function Multiply(...terms) { this.terms = terms }
function Divide(...terms) { this.terms = terms }
function Negate(...terms) { this.terms = terms }
function Log(...terms) { this.terms = terms }
function Power(...terms) { this.terms = terms }
function Sumexp(...terms) { this.terms = terms }
function Softmax(...terms) { this.terms = terms }
function Const(a) { this.value = a }
function Variable(a) { this.value = a }

const ZERO = new Const(0);
const ONE = new Const(1);
const TWO = new Const(2);
const E = new Const(Math.E);


const constructor = {
    '+' : Add,
    '-' : Subtract,
    '*' : Multiply,
    '/' : Divide,
    'negate' : Negate,
    'log' : Log,
    'pow' : Power,
    'sumexp' : Sumexp,
    'softmax' : Softmax,
};

function parse(stringValue) {
    let tokens = stringValue.split(' ').filter((token) => token.length > 0);

    let stack = [];

    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] in constructor) {
            let operationArguments = [];
            let numOfArgs = constructor[tokens[i]].prototype.operation.length;
            while (numOfArgs !== 0 && operationArguments.length !== numOfArgs) {
                operationArguments.push(stack.pop());
            }
            operationArguments.reverse();

            stack.push(new constructor[tokens[i]](...operationArguments));
            continue;
        }

        if (VARIABLES.includes(tokens[i])) {
            stack.push(new Variable(tokens[i]));
            continue;
        }

        stack.push(new Const(Number.parseInt(tokens[i])));
    }

    return stack.pop();
}

function ParsingError(message) { this.message = message }
ParsingError.prototype = Error.prototype;
ParsingError.prototype.name = "ParsingError";
ParsingError.prototype.constructor = ParsingError;

function buildParsingError(name, buildMessage) {
    function CurError(...args) { this.message = buildMessage(...args) }
    CurError.prototype = ParsingError.prototype;
    CurError.prototype.name = name;
    CurError.prototype.constructor = ParsingError;

    return CurError;
}

const UnknownTokenError = buildParsingError("UnknownTokenError",
    (pos, foundToken) => ("Unknown token " + foundToken + " at position " + pos));
const UnexpectedTokenError = buildParsingError("OperatorError",
    (pos, expected, foundToken) => "Expected " + expected + " at position " + pos + ", found " + foundToken)
const WrongNumberOfArgumentsError = buildParsingError("WrongNumberOfArgumentsError",
    (pos, operator, expected, found) => ("Wrong number of arguments for operator " + operator +
        " at position " + pos + ", expected " + expected + " found " + found))

function parsePrefSuf(stringExpression, tokenConverter, argsConverter, posConverter, closing) {
    let pos = 0;

    const skipWhitespaces = () => { while (/\s/.test(stringExpression[pos])) pos++ };

    const nextToken = () => {
        skipWhitespaces();
        const firstPos = pos;

        if (stringExpression[pos] === '(') {
            pos++;
            return "(";
        }
        if (stringExpression[pos] === ')') return ')';


        while (pos < stringExpression.length && !/\s/.test(stringExpression[pos]) &&
        stringExpression[pos] !== ')' && stringExpression[pos] !== '(') pos++;

        return stringExpression.substr(firstPos, pos - firstPos);
    };

    const parseArguments = () => {
        let args = [];
        let curToken = '';
        while (pos < stringExpression.length && curToken !== ')') {
            curToken = tokenConverter(nextToken());

            if (curToken === ')') break;

            if (curToken === '(') {
                args.push(parseFunction());
                skipWhitespaces();
                if (stringExpression[pos] !== ')') {
                    throw new UnexpectedTokenError(posConverter(pos), closing, nextToken())
                }
                pos++;
            } else if (VARIABLES.includes(curToken)) {
                args.push(new Variable(curToken));
            } else {
                if (curToken in constructor) {
                    throw new UnexpectedTokenError(posConverter(pos), "argument", curToken)
                }

                if (isNaN(+curToken)) {
                    throw new UnknownTokenError(posConverter(pos), curToken);
                }
                args.push(new Const(+curToken));
            }
            skipWhitespaces();
        }

        return argsConverter(args);
    };

    const parseFunction = function () {
        const operatorPos = pos;
        const curOperator = tokenConverter(nextToken());
        const args = parseArguments();

        if (!(curOperator in constructor)) {
            throw new UnexpectedTokenError(posConverter(operatorPos), "operator", curOperator)
        }

        const numOfArgs = constructor[curOperator].prototype.operation.length;
        if (numOfArgs !== 0 && args.length !== numOfArgs) {
            throw new WrongNumberOfArgumentsError(posConverter(operatorPos), curOperator, numOfArgs, args.length);
        }

        return new constructor[curOperator](...args);
    };

    const expression = parseArguments();
    skipWhitespaces();
    if (expression.length !== 1 || pos < stringExpression.length) {
        throw new UnexpectedTokenError(posConverter(pos), '\\n',  stringExpression.substr(pos, stringExpression.length - pos));
    }

    return expression[0]
}

function parsePrefix(stringExpression) {
    return parsePrefSuf(stringExpression, (stringValue) => stringValue,
        (args) => args,
        (pos) => pos,
        ')')
}

function parsePostfix(stringExpression) {
    return parsePrefSuf(stringExpression.split('').map((a) => a === ')' ? '(' : a === '(' ? ')' : a).
        reverse().toString().split(',').join(''),
        (stringValue) => stringValue.split('').reverse().join(''),
        (args) => args.reverse(),
        (pos) => stringExpression.length - pos,
        '(');
}
