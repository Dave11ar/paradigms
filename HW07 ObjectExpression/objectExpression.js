"use strict";

const VARIABLES =['x', 'y', 'z'];

const FUNCTIONS_CALCULATOR = {
    '+' : {
        numberOfArguments : 2,
        constructor : Add,
        evaluate : (...terms) => terms[0] + terms[1],
        diff : (variable, ...terms) => {
            return new Add(terms[0].diff(variable), terms[1].diff(variable));
        }
    },
    '-' : {
        numberOfArguments : 2,
        constructor : Subtract,
        evaluate: (...terms) => terms[0] - terms[1],
        diff : (variable, ...terms) => {
            return new Subtract(terms[0].diff(variable), terms[1].diff(variable));
        }
    },
    '*' : {
        numberOfArguments : 2,
        constructor : Multiply,
        evaluate : (...terms) => terms[0] * terms[1],
        diff : (variable, ...terms) => new Add(new Multiply(terms[0].diff(variable), terms[1]),
            new Multiply(terms[0], terms[1].diff(variable)))
    },
    '/' : {
        numberOfArguments : 2,
        constructor : Divide,
        evaluate : (...terms) => terms[0] / terms[1],
        diff : (variable, ...terms) => new Divide(new Subtract(new Multiply(terms[0].diff(variable), terms[1]),
            new Multiply(terms[0], terms[1].diff(variable))),
            new Power(terms[1], new Const(2)))
    },
    'negate' : {
        numberOfArguments : 1,
        constructor : Negate,
        evaluate: (...terms) => -terms[0],
        diff: (variable, ...terms) => new Negate(terms[0].diff(variable))
    },
    'log' : {
        numberOfArguments : 2,
        constructor : Log,
        evaluate : (...terms) => Math.log(Math.abs(terms[1])) / Math.log(Math.abs(terms[0])),
        diff : (variable, ...terms) => new Divide(
            new Subtract(new Multiply(new Multiply(new Divide(new Const(1), terms[1]),
                terms[1].diff(variable)), new Log(new Const(Math.E), terms[0])),
                new Multiply(new Multiply(new Divide(new Const(1), terms[0]),
                    terms[0].diff(variable)), new Log(new Const(Math.E), terms[1]))),
            new Power(new Log(new Const(Math.E), terms[0]), new Const(2)))
    },
    'pow' : {
        numberOfArguments : 2,
        constructor : Power,
        evaluate : (...terms) => Math.pow(terms[0], terms[1]),
        diff : (variable, ...terms) => new Add(new Multiply(new Multiply(terms[1],new Power(terms[0],
            new Subtract(terms[1], new Const(1)))), terms[0].diff(variable)), new Multiply(new Multiply(
            new Power(terms[0], terms[1]), new Log(new Const(Math.E), terms[0])), terms[1].diff(variable)))
    }
};

function makeFunction(sign, ...args) {
    Object.setPrototypeOf(this, abstractFunction);
    this.sign = sign;
    this.terms = args;
}

let abstractFunction = {
    toString : function() {
        let result = '';
        for (let curTerm of this.terms) {
            result += curTerm.toString() + ' ';
        }
        return result + this.sign;
    },
    prefix: function() {
        let result = '(' + this.sign;
        for (let curTerm of this.terms) {
            result += ' ' + curTerm.prefix();
        }
        return result + ')';
    },
    evaluate : function(...vars) {
        let evaluatedTerms = [];
        for (let i = 0; i < this.terms.length; i++) {
            evaluatedTerms.push(this.terms[i].evaluate(...vars));
        }
        return FUNCTIONS_CALCULATOR[this.sign].evaluate(...evaluatedTerms);
    },
    diff : function(variable) {
        return FUNCTIONS_CALCULATOR[this.sign].diff(variable, ...this.terms)
    }
};

function Add(...args) { makeFunction.call(this, '+', ...args) }

function Subtract(...args) { makeFunction.call(this, '-', ...args) }

function Multiply(...args) { makeFunction.call(this, '*', ...args); }

function Divide(...args) { makeFunction.call(this, '/', ...args) }

function Negate(...args) { makeFunction.call(this, 'negate', ...args)}

function Log(...args) { makeFunction.call(this, 'log', ...args) }

function Power(...args) { makeFunction.call(this, 'pow', ...args) }

function Const(...args) { this.value = args[0] }
Const.prototype = {
    evaluate : function() { return this.value },
    toString : function() { return this.value.toString() },
    prefix : function() { return this.value.toString() },
    diff : function() { return new Const(0) }
};

function Variable(...args) { this.variable = args[0] }
Variable.prototype = {
    evaluate : function(...vars) { return vars[VARIABLES.indexOf(this.variable)] },
    toString : function() { return this.variable.toString() },
    prefix : function() { return this.variable.toString() },
    diff : function(variable) { return new Const(variable === this.variable ? 1 : 0) }
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

function parsePrefix(stringExpression) {
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
        let curToken = nextToken();

        do {
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

            curToken = nextToken();
        } while (pos < stringExpression.length && curToken !== ')');

        return args;
    };

    const parseFunction = function () {
        let operatorPos = pos;
        let curOperator = nextToken();
        let args = parseArguments();

        if (!(curOperator in FUNCTIONS_CALCULATOR)) {
            throw new ParsingException("Missing operator at position: " + operatorPos);
        }

        if (args.length !== FUNCTIONS_CALCULATOR[curOperator].numberOfArguments) {
            throw new ParsingException("Wrong number of arguments for " + curOperator + "at position: " + operatorPos);
        }

        return new FUNCTIONS_CALCULATOR[curOperator].constructor(...args);
    };

    let expression = parseArguments();
    skipWhitespaces();
    if (expression.length !== 1 || pos < stringExpression.length) {
        throw new ParsingException("Redundant symbols " +
            stringExpression.substr(pos, stringExpression.length - pos) +
                " in source string after position: " + pos);
    }

    return expression[0]
}

console.log(parsePrefix('10'));
