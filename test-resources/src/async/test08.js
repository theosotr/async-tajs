var x = new Promise(function(res) {
    res(1)
});

// Check that values are propagated correctly.
var y = x
    .then() // Here, we invoke the default reactions.
    .then(function ff1(value) {
        TAJS_assertEquals(value, 1);
        return value + 1;
    })
    .then(function ff2(value) {
        TAJS_assertEquals(value, 2);
        throw 'foo';
    })
    .then(function ff3(value) {
        TAJS_dumpValue('never printed');
    })
    .catch(function ff4(reason) {
        TAJS_dumpValue('printed');
        return 'bar';
    })
    .then(function ff5(value) {
        TAJS_assertEquals(value, 'bar');
    }, function ff6(reason) {
        // Dead code.
        TAJS_dumpValue('never printed')
    })
    .then(function ff7(value) {
        // This should return undefined because
        // the previous promise returned nothing.
        TAJS_assertEquals(value, undefined);
    });
