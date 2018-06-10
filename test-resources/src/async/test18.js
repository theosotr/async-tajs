var x = Promise.resolve('foo');

x.then(function foo(value) {
    TAJS_assertEquals(value, 'foo');
    var y = new Promise(function (res) {
        res('bar');
    });
    y.then(function baz(value) {
        TAJS_assertEquals(value, 'bar');
        // This return statement is not propagated to
        // the parent promise.
        return value;
    });
}).then(function bar(value) {
    TAJS_assertEquals(value, undefined);
});
