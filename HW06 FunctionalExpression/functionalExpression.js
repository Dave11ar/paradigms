"use strict";

function cnst(...args) {
    return (x, y, z) => args[0]
}

function variable(...args) {
    return (x, y, z) => args[0] === 'x'? x : args[0] === 'y' ? y : z
}

function add(...args) {
    return (x, y, z) => args[0](x, y, z) + args[1](x, y, z)
}

function subtract(...args) {
    return (x, y, z) => args[0](x, y, z) - args[1](x, y, z)
}

function multiply(...args) {
    return (x, y, z) => args[0](x, y, z) * args[1](x, y, z)
}

function divide(...args) {
    return (x, y, z) => args[0](x, y, z) / args[1](x, y, z)
}

function negate(...args) {
    return (x, y, z) => -args[0](x, y, z)
}

function avg(...args) {
    return (x, y, z) => {
        let sum = 0;
        for (let i = 0; i < args.length; i++) {
            sum += args[i](x, y, z);
        }

        return sum / args.length;
    }
}

function med(...args) {
    return(x, y, z) => {
        let res = [];
        for (let i = 0; i < args.length; i++) {
            res.push(args[i](x, y, z));
        }

        res.sort((a, b) => (a - b));
        return res[Math.floor(args.length / 2)];
    }
}

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

let variables = ['x', 'y', 'z'];

let constants = {
    pi : cnst(Math.PI),
    e : cnst(Math.E)
};
const pi = cnst(Math.PI);
const e = cnst(Math.E);

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

        if (variables.includes(tokens[i])) {
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
