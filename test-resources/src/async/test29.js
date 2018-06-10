var x;
setTimeout(function foo() {
    setTimeout(function bar() {
        setTimeout(function baz() {
           x.foo // Hit an error.
        });
        x.foo // No error.
        x = undefined;
    });
    x = {foo : 1};
});
