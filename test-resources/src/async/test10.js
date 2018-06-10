var p1 = Promise.race('non array');
TAJS_pending(p1);
p1.catch(function function1() {
    TAJS_dumpValue('executed');
});


var p2 = Promise.race([1, 2, 3, 4]);
TAJS_pending(p2);
p2.then(function function2(val) {
    TAJS_assertEquals(val, 1);
});


var p3 = Promise.race(['foo', Promise.resolve(3), 2]);
TAJS_pending(p3);
p3.then(function function3(val) {
    TAJS_assertEquals(val, 'foo');
});


var t = {
    then: function function4(res) {
        TAJS_dumpValue('thenable is executed');
        res('thenable');
    }
};
var p4 = Promise.race([t, Promise.resolve(3), 2]);
TAJS_pending(p4);
p4.then(function function5(val) {



    TAJS_dumpValue(val); // thenable|3.0
});


var f = {
    then: function function6(res) {
        TAJS_dumpValue('thenable is executed');
        res('thenable');
    }
};
var x = new Promise(function (res, rej) {});
var p5 = Promise.race([f, x, 2]);
TAJS_pending(p5);
p5.then(function function7(val) {
    TAJS_dumpValue(val);
});


var y = new Promise(function () {});
var p6 = Promise.race([x, y]);
TAJS_pending(p6);
p6.then(function function8() {
    TAJS_dumpValue('Never printed');
});
