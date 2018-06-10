function Foo() {}

Foo.prototype.bar = function (param) {
    if (param > 1) {
        return this.getNewPromise(param);
    }
    return Promise.resolve(param);
};

Foo.prototype.getNewPromise = function(param) {
    return new Promise(function(resolve) {
        if(param > 5) {
            return Promise.reject('rejected');
        } else {
            resolve('resolved');
        }
    });
};


var o1 = new Foo();
var p1 = o1.bar(1);
TAJS_fulfilledWith(p1, 1);

var o2 = new Foo();
var p2 = o2.bar(11);
TAJS_pending(p2);

var o3 = new Foo();
var p3 = o3.bar(2);
TAJS_dumpValue(p2);
TAJS_dumpQueue();
