function foo() {
    var x = new Promise(function (res) {
        res('foo');
    });
    TAJS_dumpQueue();
    x.then(function ff1(value) {
        TAJS_dumpValue(value);
        TAJS_assertEquals(value, 'foo');
    }).catch(function ff2() {
        TAJS_dumpValue('never printed');
    }).then(function ff3(value){
        TAJS_assertEquals(value, undefined);
    });
}


foo();
