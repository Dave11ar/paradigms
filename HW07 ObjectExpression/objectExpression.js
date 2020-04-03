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
        },
        simplify : (...terms) => {
            if (terms[1] instanceof Const && terms[0] instanceof Const) {
                return new Const(terms[0].evaluate(0, 0, 0) + terms[1].evaluate(0, 0, 0));
            }
            if (terms[0] instanceof Const && terms[0].value === 0) {
                return terms[1];
            }
            if (terms[1] instanceof Const && terms[1].value === 0) {
                return terms[0];
            }
            return new Add(terms[0], terms[1]);
        }
    },
    '-' : {
        evaluate: (...terms) => terms[0] - terms[1],
        diff : (variable, ...terms) => {
            return new Subtract(terms[0].diff(variable), terms[1].diff(variable));
        },
        simplify : (...terms) => {
            if (terms[1] instanceof Const && terms[0] instanceof Const) {
                return new Const(terms[0].evaluate(0, 0, 0) - terms[1].evaluate(0, 0, 0));
            }
            if (terms[0] instanceof Const && terms[0].value === 0) {
                return (new Negate(terms[1]));
            }
            if (terms[1] instanceof Const && terms[1].value === 0) {
                return terms[0];
            }
            return new Subtract(terms[0], terms[1]);
        }
    },
    '*' : {
        evaluate : (...terms) => terms[0] * terms[1],
        diff : (variable, ...terms) => new Add(new Multiply(terms[0].diff(variable), terms[1]),
                new Multiply(terms[0], terms[1].diff(variable))),
        simplify : (...terms) => {
            if (terms[0] instanceof Const && terms[1] instanceof Const) {
                return new Const(terms[0].evaluate() * terms[1].evaluate());
            }
            if (terms[0] instanceof Const) {
                return terms[0].value === 1 ? terms[1] : (terms[0].value === 0 ? new Const(0) :
                    new Multiply(terms[0], terms[1]));
            }
            if (terms[1] instanceof Const) {
                return terms[1].value === 1 ? terms[0] : (terms[1].value === 0 ? new Const(0) :
                    new Multiply(terms[0], terms[1]));
            }
            return new Multiply(terms[0], terms[1]);
        }
    },
    '/' : {
        evaluate : (...terms) => terms[0] / terms[1],
        diff : (variable, ...terms) => new Divide(new Subtract(new Multiply(terms[0].diff(variable), terms[1]),
                new Multiply(terms[0], terms[1].diff(variable))),
                    new Power(terms[1], new Const(2))),
        simplify : (...terms) => {
            if (terms[1] instanceof Const && terms[0] instanceof Const) {
                return new Const(terms[0].evaluate() / terms[1].evaluate());
            }
            if (terms[0] instanceof Const && terms[0].value === 0) {
                return new Const(0);
            }
            if (terms[1] instanceof Const && terms[1].value === 1) {
                return terms[0];
            }

            return new Divide(terms[0], terms[1]);
        }
    },
    'negate' : {
        evaluate: (...terms) => -terms[0],
        diff: (variable, ...terms) => new Negate(terms[0].diff(variable)),
        simplify: (...terms) => terms[0] instanceof Const ? new Const(-terms[0].value) : new Negate(terms[0])
    },
    'log' : {
        evaluate : (...terms) => Math.log(Math.abs(terms[1])) / Math.log(Math.abs(terms[0])),
        diff : (variable, ...terms) => new Divide(
            new Subtract(new Multiply(new Multiply(new Divide(new Const(1), terms[1]),
                terms[1].diff(variable)), new Log(new Const(Math.E), terms[0])),
                    new Multiply(new Multiply(new Divide(new Const(1), terms[0]),
                        terms[0].diff(variable)), new Log(new Const(Math.E), terms[1]))),
                            new Power(new Log(new Const(Math.E), terms[0]), new Const(2))),
        simplify : (...terms) => {
            if (terms[0] instanceof Const && terms[1] instanceof Const) {
                return new Const((new Log(terms[0].evaluate(), terms[1].evaluate())).evaluate());
            }
            if (terms[1] instanceof Const && terms[1] === 1) {
                return new Const(0);
            }

            return new Log(terms[0], terms[1]);
        }
    },
    'pow' : {
        evaluate : (...terms) => Math.pow(terms[0], terms[1]),
        diff : (variable, ...terms) => new Add(new Multiply(new Multiply(terms[1],new Power(terms[0],
            new Subtract(terms[1], new Const(1)))), terms[0].diff(variable)), new Multiply(new Multiply(
                new Power(terms[0], terms[1]), new Log(new Const(Math.E), terms[0])), terms[1].diff(variable))),
        simplify : (...terms) => {
            if (terms[1] instanceof Const && terms[0] instanceof Const) {
                return new Const((new Power(terms[0].evaluate(), terms[1].evaluate())).evaluate());
            }
            if (terms[0] instanceof Const && terms[0].value === 0) {
                return new Const(0);
            }
            if (terms[1] instanceof Const && terms[1].value === 0) {
                return new Const(1);
            }

            return new Power(terms[0], terms[1]);
        }
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
    evaluate : function(...vars) {
        let evaluatedTerms = [];
        for (let i = 0; i < this.terms.length; i++) {
            evaluatedTerms.push(this.terms[i].evaluate(...vars));
        }
        return operationFunctionsCalculator[this.sign].evaluate(...evaluatedTerms);
    },
    diff : function(variable) {
        return operationFunctionsCalculator[this.sign].diff(variable, ...this.terms)
    },
    simplify : function() {
        for (let i = 0; i < this.terms.length; i++) {
            this.terms[i] = this.terms[i].simplify();
        }
        return operationFunctionsCalculator[this.sign].simplify(...this.terms);
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
    diff : function() { return new Const(0) },
    simplify : function() { return new Const(this.value) }
};

function Variable(...args) { this.variable = args[0] }
Variable.prototype = {
    evaluate : function(...vars) { return vars[variables.indexOf(this.variable)] },
    toString : function() { return this.variable.toString() },
    diff : function(variable) { return new Const(variable === this.variable ? 1 : 0) },
    simplify : function() { return new Variable(this.variable) }
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
