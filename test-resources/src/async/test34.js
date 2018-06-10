function foo(param) {
    return 'res: ' + param
}

var x = Promise.resolve('1');
x
    .then(foo)
    .then(function ff1(value) {
        TAJS_dumpValue(value);
        return '2'
    })
    .then(foo)
    .then(function ff2(value) {
        TAJS_dumpValue(value);
        return '3'
    })
    .then(foo)
    .then(function ff3(value) {
        TAJS_dumpValue(value);
    });

// Queue object-sensitivity is enabled.
function bar(param) {
    if (param)
        throw 'bar';
    else
        return 'foo';
}

var y = Promise.resolve(true);
y
    .then(bar)
    .then(function fb1() {
        TAJS_dumpValue('should be printed');
        return false;
    }, function fb2(reason) {
        TAJS_assertEquals(reason, 'bar');
        return false;
    })
    .then(bar)
    .then(function fb3(value) {
        TAJS_assertEquals(value, 'foo');
    }, function fb4() {
        TAJS_dumpValue('should be printed')
    });

