var resolve;
var reject;

var x = new Promise(function (res, rej) {
    resolve = res;
    reject = rej;
});

TAJS_dumpValue(x);
TAJS_dumpValue(resolve);
TAJS_dumpValue(reject);

TAJS_dumpQueue();

resolve("foo");
TAJS_dumpQueue();

// It is already resolved. Cannot be rejected.
reject("bar");
TAJS_dumpQueue();
