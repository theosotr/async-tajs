var x = new Promise(function (res, rej) {
   rej(1);
});


var y = x
    .catch(function ff1(reason) {
        TAJS_assertEquals(reason, 1);
        var z = null;
        z.x;
        TAJS_dumpValue('never printed')
    })
    .then()
    .then(function ff2() {
        TAJS_dumpValue('never printed');
    })
    .catch(function ff3(reason) {
        TAJS_dumpValue(reason);
        TAJS_dumpValue('printed here');
        throw 'foo';
        TAJS_dumpValue('never printed');
    })
    .catch()
    .then(function ff4() {
        TAJS_dumpValue('never printed');
    },
    function ff5(reason) {
        TAJS_assertEquals(reason, 'foo');
        return 'bar';
    })
    .catch('non fun')
    .then('non fun')
    .then(function ff6(value) {
        TAJS_assertEquals(value, 'bar');
        return value + 'bar'
    })
    .then('non fun', 'non fun')
    .then(function ff7(value) {
        TAJS_assertEquals(value, 'barbar');
    });
