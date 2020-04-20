"use strict";

//:NOTE: copy-paste code for operators declaration (
//     at least call for term [0] and term [1], and call `diff` method on each term in each declaration
// )

const VARIABLES =['x', 'y', 'z'];

const FUNCTIONS_CALCULATOR = {
    '+' : {
        numberOfArguments : 2, // it can be calculated automatically
        constructor : Add,
        evaluate : (...terms) => terms.reduce((a, b) => a + b, 0),
        diff : (variable, ...terms) => new Add(...terms.map((a) => a.diff(variable)))
    },
    '-' : {
        numberOfArguments : 2,
        constructor : Subtract,
        evaluate: (...terms) => terms.reduce((a, b) => a - b),
        diff : (variable, ...terms) => new Subtract(...terms.map((a) => a.diff(variable)))
    },
    '*' : {
        numberOfArguments : 2,
        constructor : Multiply,
        evaluate : (...terms) => terms.reduce((a, b) => a * b),
        diff : (variable, a, b) => new Add(new Multiply(a.diff(variable), b),
            new Multiply(a, b.diff(variable)))
    },
    '/' : {
        numberOfArguments : 2,
        constructor : Divide,
        evaluate : (...terms) => terms.reduce((a, b) => a / b),
        diff : (variable, a, b) => new Divide(new Subtract(new Multiply(a.diff(variable), b),
            new Multiply(a, b.diff(variable))), new Power(b, TWO))
    },
    'negate' : {
        numberOfArguments : 1,
        constructor : Negate,
        evaluate: (a) => -a,
        diff: (variable, a) => new Negate(a.diff(variable))
    },
    'log' : {
        numberOfArguments : 2,
        constructor : Log,
        evaluate : (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
        diff : (variable, a, b) => new Divide(
            // :NOTE: pre-define constants and use them as `Const.ZERO`, `Const.ONE`
            new Subtract(new Multiply(new Multiply(new Divide(ONE, b), b.diff(variable)), new Log(E, a)),
                new Multiply(new Multiply(new Divide(ONE, a), a.diff(variable)), new Log(E, b))),
                    new Power(new Log(E, a), TWO))
    },
    'pow' : {
        numberOfArguments : 2,
        constructor : Power,
        evaluate : (a, b) => Math.pow(a, b),
        diff : (variable, a, b) => new Add(new Multiply(new Multiply(b,new Power(a, new Subtract(b, ONE))),
            a.diff(variable)), new Multiply(new Multiply(new Power(a, b), new Log(E, a)), b.diff(variable)))
    },
    'sumexp' : {
        numberOfArguments : -1,
        constructor : Sumexp,
        evaluate : (...terms) => terms.map((a) => Math.pow(Math.E, a)).reduce((a, b) => a + b, 0),
        diff : (variable, ...terms) => new Add(...terms.map((a) => new Power(E, a))).diff(variable)
    },
    'softmax' : {
        numberOfArguments : -1,
        constructor : Softmax,
        evaluate : (...terms) => Math.pow(Math.E, terms[0]) / FUNCTIONS_CALCULATOR.sumexp.evaluate(...terms),
        diff : (variable, ...terms) => FUNCTIONS_CALCULATOR['/'].diff(variable, new Sumexp(terms[0]), new Sumexp(...terms))
    }
};

const abstractFunction = {
    toString : function() {
        return this.terms.map((a) => a.toString()).reduce((a, b) => a + ' ' + b)  + ' ' + this.sign;
    },
    prefix: function() {
        return '(' + this.sign  + (this.terms.length === 0 ?
            ' ' : this.terms.map((a) => a.prefix()).reverse().reduceRight((a, b) => a + ' ' + b , '')) + ')'
    },
    postfix: function() {
        return '(' + (this.terms.length !== 0 ?
            this.terms.map((a) => a.postfix()).reverse().reduceRight((a, b) => a + ' ' + b) : '') + ' ' + this.sign + ')'
    },
    evaluate : function(...args) {
        return FUNCTIONS_CALCULATOR[this.sign].evaluate(...this.terms.map((a) => a.evaluate(...args)));
    },
    diff : function(variable) {
        return FUNCTIONS_CALCULATOR[this.sign].diff(variable, ...this.terms)
    }
};

function makeFunction(sign, ...args) {
    Object.setPrototypeOf(this, abstractFunction);
    this.sign = sign;
    this.terms = args;
}

function Add(...args) { makeFunction.call(this, '+', ...args) }
function Subtract(...args) { makeFunction.call(this, '-', ...args) }
function Multiply(...args) { makeFunction.call(this, '*', ...args); }
function Divide(...args) { makeFunction.call(this, '/', ...args) }
function Negate(...args) { makeFunction.call(this, 'negate', ...args)}
function Log(...args) { makeFunction.call(this, 'log', ...args) }
function Power(...args) { makeFunction.call(this, 'pow', ...args) }
function Sumexp(...args) { makeFunction.call(this, 'sumexp', ...args) }
function Softmax(...args) { makeFunction.call(this, 'softmax', ...args) }

function Const(a) { this.value = a }
Const.prototype = {
    evaluate : function() { return this.value },
    toString : function() { return this.value.toString() },
    prefix : function() { return this.value.toString() },
    postfix : function() { return this.value.toString() },
    diff : () => ZERO
};
const ZERO = new Const(0);
const ONE = new Const(1);
const TWO = new Const(2);
const E = new Const(Math.E);

function Variable(a) { this.variable = a }
Variable.prototype = {
    evaluate : function(...vars) { return vars[VARIABLES.indexOf(this.variable)] },
    toString : function() { return this.variable.toString() },
    prefix : function() { return this.variable.toString() },
    postfix : function() { return this.variable.toString() },
    diff : function(variable) { return variable === this.variable ? ONE : ZERO }
};

function parse(stringValue) {
    let tokens = stringValue.split(' ').filter((token) => token.length > 0);

    let stack = [];

    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] in FUNCTIONS_CALCULATOR) {
            let operationArguments = [];
            while (operationArguments.length !== FUNCTIONS_CALCULATOR[tokens[i]].numberOfArguments) {
                operationArguments.push(stack.pop());
            }
            operationArguments.reverse();

            stack.push(new FUNCTIONS_CALCULATOR[tokens[i]].constructor(...operationArguments));
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

function ParsingException(message) { this.message = message }
ParsingException.prototype = Error.prototype;
ParsingException.prototype.name = "ParsingException";

function parsePrefSuf(stringExpression, tokenConverter, argsConverter) {
    let pos = 0;

    const skipWhitespaces = () => { while (/\s/.test(stringExpression[pos])) pos++ };

    const nextToken = () => {
        skipWhitespaces();
        let firstPos = pos;

        if (stringExpression[pos] === '(') {
            pos++;
            return "(";
        }
        if (stringExpression[pos] === ')') return ")";


        while (pos < stringExpression.length && !/\s/.test(stringExpression[pos]) &&
        stringExpression[pos] !== ')' && stringExpression[pos] !== '(') pos++;

        return stringExpression.substr(firstPos, pos - firstPos);
    };

    const parseArguments = () => {
        let args = [];
        let curToken;
        while (pos < stringExpression.length && curToken !== ')') {
            curToken = tokenConverter(nextToken());

            if (curToken === ')') break;

            if (curToken === '(') {
                args.push(parseFunction());
                skipWhitespaces();
                if (stringExpression[pos++] !== ')') {
                    throw new ParsingException("Missing opening parenthesis for parenthesis at position: " + pos);
                }
            } else if (VARIABLES.includes(curToken)) {
                args.push(new Variable(curToken));
            } else {
                if (curToken in FUNCTIONS_CALCULATOR) {
                    throw new ParsingException("Expected argument, actual operator: " + curToken +
                        ' at position: ' + (pos - curToken.length));
                }

                if (isNaN(+curToken)) {
                    throw new ParsingException("Unknown token: " + curToken + " at position: " + (pos - curToken.length));
                }
                args.push(new Const(+curToken));
            }
            skipWhitespaces();
        }
        skipWhitespaces();
        return argsConverter(args);
    };

    const parseFunction = function () {
        let operatorPos = pos;
        let curOperator = tokenConverter(nextToken());
        let args = parseArguments();

        if (!(curOperator in FUNCTIONS_CALCULATOR)) {
            throw new ParsingException("Missing operator at position: " + operatorPos);
        }

        if (FUNCTIONS_CALCULATOR[curOperator].numberOfArguments !== -1 &&
            args.length !== FUNCTIONS_CALCULATOR[curOperator].numberOfArguments) {
            throw new ParsingException("Wrong number of arguments for " + curOperator + "at position: " + operatorPos);
        }

        return new FUNCTIONS_CALCULATOR[curOperator].constructor(...args);
    };

    let expression = parseArguments();
    skipWhitespaces();
    if (expression.length !== 1 || pos < stringExpression.length) {
        throw new ParsingException("Redundant symbols" +
            stringExpression.substr(pos, stringExpression.length - pos) +
            " in source string after position: " + pos);
    }

    return expression[0]
}

function parsePrefix(stringExpression) {
    return parsePrefSuf(stringExpression, (stringValue) => stringValue, (args) => args)
}

function parsePostfix(stringExpression) {
    let reverseBuff = [];

    for (let i = stringExpression.length - 1; i >= 0; i--) {
        if (stringExpression[i] === ')') {
            reverseBuff.push('(');
        } else if (stringExpression[i] === '(') {
            reverseBuff.push(')')
        } else {
            reverseBuff.push(stringExpression[i]);
        }
    }
    return parsePrefSuf(reverseBuff.toString().split(',').join(''),
        (stringValue) => stringValue.split('').reverse().join(''),
            (args) => args.reverse());
}

//console.log((new Const(10).diff('x')).evaluate(2.0,2.0,2.0));
console.log(ZERO.evaluate(2,2,2));
