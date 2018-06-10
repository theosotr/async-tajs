try {
    x = Promise(function (res, rej) {
    });
    TAJS_dumpValue("never reach here 1");
} catch (e) {
    TAJS_dumpValue("error 1");
}

try {
    x = new Promise();
    TAJS_dumpValue("never reach here 2");
} catch (e) {
    TAJS_dumpValue("error 2");
}

try {
    x = new Promise(1);
    TAJS_dumpValue("never reach here 3");
} catch (e) {
    TAJS_dumpValue("error promise argument is not a function");
}


TAJS_dumpQueue(); // Queue contains only native objects.
