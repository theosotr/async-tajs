var t;

var x = new Promise(function(res) {
    setTimeout(res, 0, 'bar');
    t = {foo: 1};
});
x.then(function fun1(value) {
    TAJS_assertEquals(value, 'bar');
    t.foo; // Hit an error.
});


var y = Promise.reject('foo');
y.catch(function fun2(reason) {
    t = undefined;
    TAJS_assertEquals(reason, 'foo');
});
