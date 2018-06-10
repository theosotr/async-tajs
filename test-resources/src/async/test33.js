var x = new Promise(function (res) {
    setTimeout(res, 0, 'bar');
});
var y = new Promise(function (res) {
    setTimeout(res, 0, 'foo')
});

var z;
x.then(function foo1(value) {
    z = {foo: 1};
    TAJS_assertEquals(value, 'bar');
    return 'foo'
}).then(function foo2(value) {
    TAJS_assertEquals(value, 'foo')
});


y.then(function bar1(value) {
    z.foo;
    TAJS_assertEquals(value, 'foo');
    return 'baz';
}).then(function bar2(value) {
    TAJS_dumpValue(value, 'baz');
}, function bar3() {
    TAJS_dumpValue('catch executed')
});
