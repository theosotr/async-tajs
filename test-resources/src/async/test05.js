// Case 1: A simple call to resolve.
var val = "foo";
var x = Promise.resolve(val);
TAJS_assertEquals(x instanceof Promise, true);
TAJS_fulfilledWith(x, val);


// Case 2: A simple call to reject.
var y = Promise.reject(val);
TAJS_assertEquals(y instanceof Promise, true);
TAJS_rejectedWith(y, val);


// Case 3: Resolve an object with a property `then` (non-callable).
var o = {then: 1};
x = Promise.resolve(o);
TAJS_fulfilledWith(x, o);


// Case 4: Resolution of a promise simply returns the same promise.
var z = Promise.resolve(x);
TAJS_assertEquals(x, z);


o.then = function ff1(res) {
    res("resolve asynchronously");
};
// Case 5: Reject an thenable object.
z = Promise.reject(o); // This does not lead to the execution of `then()`.
TAJS_rejectedWith(z, o);


// Case 6: Resolve a thenable. Leads to the asynchronous execution of
// `then()` of object o.
z = Promise.resolve(o)
    .then(function ff2(value) {
        o.then;
        TAJS_assertEquals(value, "resolve asynchronously");
    });
