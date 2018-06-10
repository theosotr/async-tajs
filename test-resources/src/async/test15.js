var x;

setTimeout(function () {
    x = {foo: 1};
});

setTimeout(function () {
    var y;
    setTimeout(function () {
        y.bar;
        TAJS_dumpValue('should be printed');
        setTimeout(function () {
            y.bar.bar;
            TAJS_dumpValue('should be printed');
            y.bar.bar.bar;
            TAJS_dumpValue('never printed');
        });
    });
    y = {bar: x.foo};
    TAJS_dumpValue('should be printed')
});
