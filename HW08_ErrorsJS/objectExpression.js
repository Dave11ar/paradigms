'use strict';

// :NOTE: common mistakes: 7

const VARIABLES =['x', 'y', 'z'];

const abstractFunction = {
    _formatOutput : function(leftBorder, rightBorder, stringFunction) {
        return leftBorder + this.terms.map(stringFunction).join(' ') + rightBorder
    },
    // :NOTE: almost good but `function ()` is useless
    toString : function() { return this._formatOutput('', this.sign, (a) => a.toString()) },
    prefix : function() { return this._formatOutput('(' + this.sign + ' ', ')', (a) => a.prefix())},
    postfix : function() { return this._formatOutput('(', ' ' + this.sign + ')', (a) => a.postfix())},
    evaluate : function (...args) { return this.operation(...this.terms.map((a) => a.evaluate(...args))) },
};

function OperationFactory(sign, operation, diff) {
    let func = function (...terms) {
        this.terms = terms;
    }
    const protoOperation = {
        sign : sign,
        operation : operation,
        diff : diff
    }
    Object.setPrototypeOf(protoOperation, abstractFunction);

    func.prototype = protoOperation;
    return func;
}

const Add = OperationFactory(
    '+',
    (a, b) => a + b,
    function(variable) { return  new Add(...this.terms.map((a) => a.diff(variable))) }
);

const Subtract = OperationFactory(
    '-',
    (a, b) => a - b,
    function(variable) { return new Subtract(...this.terms.map((a) => a.diff(variable))) }
);
const Multiply = OperationFactory(
    '*',
    (a, b) => a * b,
    function(variable) { return  new Add(new Multiply(this.terms[0].diff(variable), this.terms[1]),
        new Multiply(this.terms[0], this.terms[1].diff(variable))) }
);
const Divide = OperationFactory(
    '/',
    (a, b) => a / b,
    function(variable) { return new Divide(new Subtract(new Multiply(this.terms[0].diff(variable), this.terms[1]),
        new Multiply(this.terms[0], this.terms[1].diff(variable))), new Power(this.terms[1], TWO)) }
);
const Negate = OperationFactory(
    'negate',
    (a) => -a,
    function(variable) { return new Negate(this.terms[0].diff(variable)) }
);
const Log = OperationFactory(
    'log',
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    function(variable) { return new Divide(new Subtract(new Multiply(new Multiply(new Divide(ONE, this.terms[1]),
        this.terms[1].diff(variable)), new Log(E, this.terms[0])), new Multiply(new Multiply(
        new Divide(ONE, this.terms[0]), this.terms[0].diff(variable)), new Log(E, this.terms[1]))),
        new Power(new Log(E, this.terms[0]), TWO)) }
);
const Power = OperationFactory(
    'pow',
    (a, b) => Math.pow(a, b),
    function(variable) { return new Add(new Multiply(new Multiply(this.terms[1], new Power(this.terms[0],
        new Subtract(this.terms[1], ONE))), this.terms[0].diff(variable)), new Multiply(new Multiply(
        new Power(this.terms[0], this.terms[1]), new Log(E, this.terms[0])), this.terms[1].diff(variable))) }
);
const Sumexp = OperationFactory(
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
const Softmax = OperationFactory(
    'softmax',
    (...terms) =>  Math.pow(Math.E, terms[0]) / terms.map((a) => Math.pow(Math.E, a)).reduce(
        (a, b) => a + b, 0),
    function(variable) {
        if (this.terms.length === 0) return ZERO;
        let first = new Power(E, this.terms[0]);
        return new Divide(first, new Sumexp(...this.terms)).diff(variable);
    }
);

// :NOTE: this still looks copy-pasted
const abstractTerminal = {
    toString : function() { return this.value.toString() },
    prefix : function() { return this.value.toString() },
    postfix : function() { return this.value.toString() },
}
function TerminalFactory(evaluate, diff) {
    let func = function(value) {
        this.value = value;
    }

    const protoTerminal = {
        evaluate : evaluate,
        diff : diff
    }
    Object.setPrototypeOf(protoTerminal, abstractTerminal);
    func.prototype = protoTerminal;

    return func;
}

const Variable = TerminalFactory(
    function(...vars) { return vars[VARIABLES.indexOf(this.value)] },
    function(variable) { return (variable === this.value ? ONE : ZERO) }
);
const Const = TerminalFactory(
    function() { return this.value },
    () => ZERO
);

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
function errorFactory(proto, name, buildMessage) {
    function CurError(...args) { this.message = buildMessage(...args) }
    CurError.prototype = Object.create(proto.prototype);
    CurError.prototype.name = name;
    CurError.constructor = proto;

    return CurError;
}

const ParsingError = errorFactory(Error, 'ParsingError', (message) => message);

const UnknownTokenError = errorFactory(ParsingError, 'UnknownTokenError',
    (pos, foundToken) => ('Unknown token ' + foundToken + ' at position ' + pos));
const UnexpectedTokenError = errorFactory(ParsingError, 'OperatorError',
    (pos, expected, foundToken) => 'Expected ' + expected + ' at position ' + pos + ', found ' + foundToken)
const WrongNumberOfArgumentsError = errorFactory(ParsingError, 'WrongNumberOfArgumentsError',
    (pos, operator, expected, found) => ('Wrong number of arguments for operator ' + operator +
        ' at position ' + pos + ', expected ' + expected + ' found ' + found))
const BracketNotFoundError = errorFactory(UnexpectedTokenError, 'BracketNotFoundError',
    (pos, expected, foundToken) => 'Expected ' + expected + ' at position ' + pos + ', found ' + foundToken)
const EndOfFiletNotFoundError = errorFactory(UnexpectedTokenError, 'EndOfFiletNotFoundError',
    (pos, foundToken) => 'Expected end of file at position ' + pos + ', found ' + foundToken)

function parsePrefSuf(stringExpression, tokenConverter, argsConverter, posConverter, closing) {
    let pos = 0;

    function skipWhitespaces() { while (/\s/.test(stringExpression[pos])) pos++ }

    function test(condition, curError, ...args) {
        if (condition) {
            throw new curError(...args);
        }
    }

    function nextToken() {
        skipWhitespaces();
        const firstPos = pos;

        if (stringExpression[pos] === '(') {
            pos++;
            return '(';
        }
        if (stringExpression[pos] === ')') return ')';


        while (pos < stringExpression.length && !/\s/.test(stringExpression[pos]) &&
        stringExpression[pos] !== ')' && stringExpression[pos] !== '(') pos++;

        return stringExpression.substr(firstPos, pos - firstPos);
    }

    function parseArguments() {
        let args = [];
        let curToken = '';
        while (pos < stringExpression.length && curToken !== ')') {
            curToken = tokenConverter(nextToken());

            if (curToken === ')') break;

            if (curToken === '(') {
                args.push(parseFunction());
                skipWhitespaces();
                test(stringExpression[pos] !== ')', BracketNotFoundError,
                    posConverter(pos), closing, nextToken())
                pos++;
            } else if (VARIABLES.includes(curToken)) {
                args.push(new Variable(curToken));
            } else {
                test(curToken in constructor, UnexpectedTokenError,
                    posConverter(pos), 'argument', curToken)
                test(isNaN(+curToken), UnknownTokenError,
                    posConverter(pos), curToken)

                args.push(new Const(+curToken));
            }
            skipWhitespaces();
        }

        return argsConverter(args);
    }

    function parseFunction() {
        const operatorPos = pos;
        const curOperator = tokenConverter(nextToken());
        const args = parseArguments();

        test(!(curOperator in constructor), UnexpectedTokenError,
            posConverter(operatorPos), 'operator', curOperator)

        const numOfArgs = constructor[curOperator].prototype.operation.length;
        test(numOfArgs !== 0 && args.length !== numOfArgs, WrongNumberOfArgumentsError,
            posConverter(operatorPos), curOperator, numOfArgs, args.length)

        return new constructor[curOperator](...args);
    }

    const expression = parseArguments();
    skipWhitespaces();
    test(expression.length !== 1 || pos < stringExpression.length, EndOfFiletNotFoundError,
        posConverter(pos),  stringExpression.substr(pos, stringExpression.length - pos))

    return expression[0]
}

function parsePrefix(stringExpression) {
    return parsePrefSuf(stringExpression, (stringValue) => stringValue,
        (args) => args,
        (pos) => pos,
        ')')
}

function parsePostfix(stringExpression) {
    return parsePrefSuf(
        stringExpression.split('').map((a) => a === ')' ? '(' : a === '(' ? ')' : a).
        reverse().toString().split(',').join(''),
        (stringValue) => stringValue.split('').reverse().join(''),
        (args) => args.reverse(),
        (pos) => stringExpression.length - pos,
        '(');
}
