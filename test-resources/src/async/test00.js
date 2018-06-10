var x = new Promise(function (res, rej) {
    TAJS_dumpValue(res);
    TAJS_dumpValue(rej);
});

TAJS_dumpValue(x);
TAJS_dumpQueue();
TAJS_assertEquals(x instanceof Promise, true);
