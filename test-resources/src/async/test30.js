var t;
function foo() {
    if (TAJS_make('AnyBool')) {
        return Promise.resolve('foo').then(
            function fun2() {
                t = {bar: 1};
                return 'foo';
            }
        ).then(function fun3(value) {
            TAJS_assertEquals(value, 'foo');
            throw 'baz';
        });
    } else {
        return Promise.resolve({
            then: function fun4(res) {
                TAJS_dumpValue('thenable exec');
                t = {bar : 1};
                res('bar');
            }
        });
    }
}


var x = Promise.resolve('bar');
x
    .then(function fun1(value) {
        TAJS_assertEquals(value, 'bar');
        return foo();
    })
    .then(function fun5(value) {
        t.bar; // no error;
        TAJS_assertEquals(value, 'bar');
    })
    .catch(function fun7(value) {
        // This error is propagated from fun3.
        TAJS_dumpValue(value);
    });
