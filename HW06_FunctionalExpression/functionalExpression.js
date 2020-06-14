"use strict";

const operation = (func) => ((...args) => (...variables) => func(...args.map(arg => arg(...variables))));

const add = operation((a, b) => a + b);
const subtract = operation((a, b) => a - b);
const multiply = operation((a, b) => a * b);
const divide = operation((a, b) => a / b);
const negate = operation((a) => -a);

const cnst = (a) => () => a;
const variable = (a) => (...vars) => vars[variables[a]];

const avg = operation((...args) => args.reduce((a, b) => a + b) / args.length);
const med = operation((...args) => args.sort((a, b) => (a - b))[Math.floor(args.length / 2)]);

let functions = {
    '+' : add,
    '-' : subtract,
    '*' : multiply,
    '/' : divide,
    'negate' : negate,
    'avg5' : avg,
    'med3' : med
};
const avg5 = avg;
const med3 = med;

let numberOfArgs = {
    '+' : 2,
    '-' : 2,
    '*' : 2,
    '/' : 2,
    'negate' : 1,
    'avg5' : 5,
    'med3' : 3
};

const variables = {
    'x' : 0,
    'y' : 1,
    'z' : 2
};

const constants = {
    pi : cnst(Math.PI),
    e : cnst(Math.E)
};

const pi = constants.pi;
const e = constants.e;

function parse(stringValue) {
    let tokens = stringValue.split(' ').filter((token) => token.length > 0);

    let stack = [];

    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] in numberOfArgs) {
            let operationArguments = [];
            while (operationArguments.length !== numberOfArgs[tokens[i]]) {
                operationArguments.push(stack.pop());
            }
            operationArguments.reverse();

            stack.push(functions[tokens[i]](...operationArguments));
            continue;
        }

        if (tokens[i] in variables) {
            stack.push(variable(tokens[i]));
            continue;
        }

        if (tokens[i] in constants) {
            stack.push(constants[tokens[i]]);
            continue;
        }

        stack.push(cnst(Number.parseInt(tokens[i])));
    }

    return stack.pop();
}
