var y;
var p1 = new Promise(function (res) {
    y = res;
});

TAJS_dumpValue(y);
TAJS_pending(p1);
y("bar");
TAJS_fulfilledWith(p1, "bar");

var p2 = new Promise(function (res, rej) {
    y = rej;
});

TAJS_dumpValue(y);
TAJS_pending(p2);
y("rej");
TAJS_rejectedWith(p2, "rej");
