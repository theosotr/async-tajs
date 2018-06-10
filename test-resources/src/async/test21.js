var fs = require('fs');

var y;
fs.readFile('path', undefined, function foo(err, data) {
    var x;
    TAJS_dumpValue('executed');
    if (err) {
        TAJS_dumpValue('erroneous path');
    } else {
        TAJS_dumpValue('correct path');
    }
    TAJS_dumpValue(err);
    TAJS_dumpValue(data);
    fs.readFile('path',  undefined, function bar(err, data) {
        if (err) {
            TAJS_dumpValue('erroneous path');
        } else {
            TAJS_dumpValue('correct path');
        }
        TAJS_dumpValue(err);
        TAJS_dumpValue(data);
        TAJS_dumpValue('executed nested');
        x.foo;
        TAJS_dumpValue('printed');
    });
    x = {foo: y.bar};
    TAJS_dumpValue('printed');
});


fs.readFile('path', undefined, function baz(err, data) {
    y = {bar: 1};
    TAJS_dumpValue(err);
    TAJS_dumpValue(data);
});
