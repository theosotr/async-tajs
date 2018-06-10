var x = new Promise(function(res) {
    setTimeout(res, 0, 'bar');
});

var y;
x.then(function ff1(value) {
    TAJS_assertEquals(value, 'bar');
    setTimeout(function ff2() {
        y.foo ; // no error here
        TAJS_dumpValue('executed');
        var e = Promise.resolve('bar');
        var z = {foo : 1};
        e.then(function ff3(value) {
            TAJS_assertEquals(value, 'bar');
            var t = z.foo;
            z = undefined;
            return t;
        }).then(function ff4(value) {
            TAJS_assertEquals(value, 1);
            z.foo;
        }).catch(function ff5() {
            TAJS_dumpValue('catch executed');
        })
    });
    return 'foo'
}).then(function ff6(value) {
    y = {foo: 1};
    TAJS_assertEquals(value, 'foo');
});
