// Case 1: Check that the callback is registered
function foo() { }
setTimeout(foo);
TAJS_hasTimerCallback("setTimeout", foo);

// Case 2: Check that the same callback is registered twice.
setTimeout(foo);
TAJS_dumpQueue();

// Case 3: Check that the callback is invoked with
// the appropriate parameters.
function bar(param1, param2, param3) {
    TAJS_assertEquals(param1, 'foo');
    TAJS_assertEquals(param2, 'bar');
    TAJS_assertEquals(param3, undefined);
}
setTimeout(bar, 1, 'foo', 'bar');


setTimeout(function baz() {
    setTimeout(function lala(x) {
        x.foo.foo; // x does not have property foo.
        TAJS_dumpValue("never printed");
    }, 1, {bar: 2});
    TAJS_dumpValue("should be printed");
});
