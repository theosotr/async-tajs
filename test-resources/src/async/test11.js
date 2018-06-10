// Case 1: resolve promise asynchronously.
var p1 = new Promise(function(resolve) {
    setTimeout(resolve, 0, 'foo');
});
TAJS_pending(p1);
p1.then(function ff1(value) {
    TAJS_assertEquals(value, 'foo');
});

// Case 2: reject promise asynchronously
var p2 = new Promise(function (resolve, reject) {
    setTimeout(function ff2() {
        reject('bar');
    })
});
TAJS_pending(p2);
p2.catch(function ff3(reason) {
   TAJS_assertEquals(reason, 'bar');
});
