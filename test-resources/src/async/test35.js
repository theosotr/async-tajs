var bar = false;
var z;

function foo(promise, param) {
    TAJS_makeContextSensitive(foo, 0);
    promise.then(function bar() {
        if (param)
            z.foo;
    });
}

var x = Promise.resolve();
foo(Promise.resolve(), bar);
x.then(function baz() {

});
Promise.resolve().then(function qux() {
    bar = true;
    z = {foo: 1};
});
foo(Promise.resolve(), bar);
Promise.resolve().then(function quux() {

});
foo(Promise.resolve(), bar);
