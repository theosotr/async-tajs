var t = {};

var p1 = new Promise(function (res) {
    res(t);
});
TAJS_fulfilledWith(p1, t);

t = {then: 1};
p2 = new Promise(function (res) {
   res(t);
});
TAJS_fulfilledWith(p2, t);

var p3;
t = {
    then: function ff1(res, rej) {
        TAJS_pending(p3);
        TAJS_dumpValue(res);
        TAJS_dumpValue(rej);
    }
};
p3 = new Promise(function (res) {
    y = res;
    res(t);
});

TAJS_fulfilledWith(t);
TAJS_pending(p3);
TAJS_dumpQueue();
y("val"); // This resolution has not any effect.
TAJS_pending(p3);

p3.then(function ff2(value) {
   TAJS_dumpValue("Never printed"); // This is never executed.
});

// Case 4: Promise is resolved.
var p4;
t2 = {
    then: function ff3(res) {
        res("foo");
        TAJS_dumpValue('never printed');
    }
};
p4 = new Promise(function (res) {
    res(t2);
});
TAJS_pending(p4);
p4.then(function ff4(value) {
   TAJS_assertEquals(value, "foo"); // This should always be executed
});
