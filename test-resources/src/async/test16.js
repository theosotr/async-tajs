var x = Promise.resolve('foo');
var y = Promise.reject('bar');
var z;

// TODO revisit
x.then(function foo(value) {
    TAJS_assertEquals(value, 'foo');
    y.catch(function bar(reason) {
        TAJS_assertEquals(reason, 'bar');
        z.foo;
    });
    z = {foo: 1};
    TAJS_dumpValue('should be printed');
});

var t;
x.then(function baz() {
    t = {foo: 1};
}).then(function la() {
    t = {foo: {bar: 2}}
});
x.then(function lala() {
    TAJS_dumpValue(t);
    t.foo.bar;
});
