var x = new Promise(function () {
    throw 'foo';
});

TAJS_rejectedWith(x, 'foo');


var y, z, t = {};

x = new Promise(function (res) {
   y = new Promise (function (res, rej) {
       z = new Promise(function () {
           throw 'foo';
           TAJS_dumpValue('never printed');
       });
       rej('bar');
       TAJS_dumpValue('never printed');
   });
   res('res');
   t = new Promise(function (res, rej) {

   });
   TAJS_dumpValue('never printed');
});

TAJS_fulfilledWith(x, 'res');
TAJS_rejectedWith(y, 'bar');
TAJS_rejectedWith(z, 'foo');
TAJS_notInQueue(t);