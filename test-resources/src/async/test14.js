var x;

setTimeout(function ff1() {
    var y;
    setTimeout(function ff2() {
        y.bar;
        setTimeout(function ff3() {
            y.bar.bar;
            y.bar.bar.bar;
            TAJS_dumpValue('never printed');
        });
    });
    y = {bar: x.foo};
});

x = {foo: 1};
