"use strict";

let functions = {
    '+' : { constructor : Add, numberOfArguments : 2 },
    '-' : { constructor : Subtract, numberOfArguments : 2 },
    '*' : { constructor : Multiply, numberOfArguments : 2 },
    '/' : { constructor : Divide, numberOfArguments : 2 },
    'negate' : { constructor : Negate, numberOfArguments : 1 },
    'log' : { constructor : Log, numberOfArguments : 2 },
    'pow' : { constructor : Power, numberOfArguments : 2 },
};

let variables =['x', 'y', 'z'];

let operationFunctionsCalculator = {
    '+' : {
        evaluate : (...terms) => terms[0] + terms[1],
        diff : (variable, ...terms) => {
            return new Add(terms[0].diff(variable), terms[1].diff(variable));
        }
    },
    '-' : {
        evaluate: (...terms) => terms[0] - terms[1],
        diff : (variable, ...terms) => {
            return new Subtract(terms[0].diff(variable), terms[1].diff(variable));
        }
    },
    '*' : {
        evaluate : (...terms) => terms[0] * terms[1],
        diff : (variable, ...terms) => new Add(new Multiply(terms[0].diff(variable), terms[1]),
            new Multiply(terms[0], terms[1].diff(variable)))
    },
    '/' : {
        evaluate : (...terms) => terms[0] / terms[1],
        diff : (variable, ...terms) => new Divide(new Subtract(new Multiply(terms[0].diff(variable), terms[1]),
            new Multiply(terms[0], terms[1].diff(variable))),
            new Power(terms[1], new Const(2)))
    },
    'negate' : {
        evaluate: (...terms) => -terms[0],
        diff: (variable, ...terms) => new Negate(terms[0].diff(variable))
    },
    'log' : {
        evaluate : (...terms) => Math.log(Math.abs(terms[1])) / Math.log(Math.abs(terms[0])),
        diff : (variable, ...terms) => new Divide(
            new Subtract(new Multiply(new Multiply(new Divide(new Const(1), terms[1]),
                terms[1].diff(variable)), new Log(new Const(Math.E), terms[0])),
                new Multiply(new Multiply(new Divide(new Const(1), terms[0]),
                    terms[0].diff(variable)), new Log(new Const(Math.E), terms[1]))),
            new Power(new Log(new Const(Math.E), terms[0]), new Const(2)))
    },
    'pow' : {
        evaluate : (...terms) => Math.pow(terms[0], terms[1]),
        diff : (variable, ...terms) => new Add(new Multiply(new Multiply(terms[1],new Power(terms[0],
            new Subtract(terms[1], new Const(1)))), terms[0].diff(variable)), new Multiply(new Multiply(
            new Power(terms[0], terms[1]), new Log(new Const(Math.E), terms[0])), terms[1].diff(variable)))
    }
};

function Operation(sign, ...args) {
    Object.setPrototypeOf(this, operationFunctions);
    this.sign = sign;
    this.terms = args;
}

let operationFunctions = {
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
        return operationFunctionsCalculator[this.sign].evaluate(...evaluatedTerms);
    },
    diff : function(variable) {
        return operationFunctionsCalculator[this.sign].diff(variable, ...this.terms)
    }
};

function Add(...args) { Operation.call(this, '+', ...args) }

function Subtract(...args) { Operation.call(this, '-', ...args) }

function Multiply(...args) { Operation.call(this, '*', ...args); }

function Divide(...args) { Operation.call(this, '/', ...args) }

function Negate(...args) { Operation.call(this, 'negate', ...args)}

function Log(...args) { Operation.call(this, 'log', ...args) }

function Power(...args) { Operation.call(this, 'pow', ...args) }

function Const(...args) { this.value = args[0] }
Const.prototype = {
    evaluate : function() { return this.value },
    toString : function() { return this.value.toString() },
    prefix : function() { return this.value.toString() },
    diff : function() { return new Const(0) }
};

function Variable(...args) { this.variable = args[0] }
Variable.prototype = {
    evaluate : function(...vars) { return vars[variables.indexOf(this.variable)] },
    toString : function() { return this.variable.toString() },
    prefix : function() { return this.variable.toString() },
    diff : function(variable) { return new Const(variable === this.variable ? 1 : 0) }
};

function parse(stringValue) {
    let tokens = stringValue.split(' ').filter((token) => token.length > 0);

    let stack = [];

    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] in functions) {
            let operationArguments = [];
            while (operationArguments.length !== functions[tokens[i]].numberOfArguments) {
                operationArguments.push(stack.pop());
            }
            operationArguments.reverse();

            stack.push(new functions[tokens[i]].constructor(...operationArguments));
            continue;
        }

        if (variables.includes(tokens[i])) {
            stack.push(new Variable(tokens[i]));
            continue;
        }

        stack.push(new Const(Number.parseInt(tokens[i])));
    }

    return stack.pop();
}

const parsePrefix = function(stringValue) {
    let pos = 0;


    const skipWhitespaces = () => {
        while (stringValue[pos] === ' ') pos++
    };

    const nextToken = () => {
        skipWhitespaces();
        let firstPos = pos;

        if (stringValue[pos] === '(') {
            pos++;
            return "(";
        }

        while (pos < stringValue.length && stringValue[pos] !== ' ' && stringValue[pos] !== ')' &&
            stringValue[pos] !== '(') pos++;

        return stringValue.substr(firstPos, pos - firstPos);
    };

    const parse = function () {
        let curOperator = nextToken();

        if (curOperator === '(') {
            let value = parse();
            skipWhitespaces();
            if (pos === stringValue.length || stringValue[pos++] !== ')') {
                throw new Error(1);
            }
            return value;
        }

        if (!(curOperator in functions)) {
            if (variables.includes(curOperator)) {
                skipWhitespaces();
                if (pos !== stringValue.length) {
                    throw new Error(1);
                }

                return new Variable(curOperator);
            }

            if (curOperator === '(') {
                let value = parse();
                skipWhitespaces();
                if (pos === stringValue.length ||stringValue[pos++] !== ')') {
                    throw new Error(1);
                }
                return value;
            }

            let value = Number.parseInt(curOperator);
            skipWhitespaces();
            if (isNaN(value) || pos !== stringValue.length) {
                throw new Error(1);
            }
            return new Const(value);
        }
        let args = [];

        while (pos < stringValue.length && stringValue[pos] !== ')') {
            let curToken = nextToken();

            if (variables.includes(curToken)) {
                args.push(new Variable(curToken));
                skipWhitespaces();
                continue;
            }

            if (curToken === '(') {
                args.push(parse());
                skipWhitespaces();
                if (pos === stringValue.length || stringValue[pos++] !== ')') {
                    throw new Error(1);
                }skipWhitespaces();
                continue;
            }


            //check bad const
            let value = Number.parseInt(curToken);

            if (isNaN(value) || isNaN(+curToken)) {
                throw new Error(1);
            }
            args.push(new Const(value));
            skipWhitespaces();
        }

        if (args.length !== functions[curOperator].numberOfArguments) {
            throw new Error(1);
        }


        return new functions[curOperator].constructor(...args);
    };

    let value = parse();
    skipWhitespaces();
    if (pos < stringValue.length && stringValue[pos] === ')') {
        throw new Error(1);
    }
    return value
};

let a = parsePrefix('(- (+ (* x x ) (*  5 (* z (* z z)) )) (* y 8))');
console.log(a.evaluate(1,0,0));
