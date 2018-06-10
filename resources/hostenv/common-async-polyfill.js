(function (global) {
    var UInt = TAJS_make("AnyNumUInt");

    function _Promise() { }

    global.Promise = function(f) {
        var p = new _Promise();
        p.__proto__ = this.__proto__;
        TAJS_promisify(p);
        try {
            // Here, we synchronously execute the function passed in
            // Promise constructor.
            f(function (val) {
                TAJS_resolve(p, val);
            },
            function (val) {
                TAJS_reject(p, val);
            });
        } catch (e) {
            TAJS_reject(p, e);
        }
        return p;
    };

    global.Promise.resolve = function(val) {
        if (this === val.constructor)
            return val;
        var then = val.then;
        var p = new _Promise();
        p.__proto__ = this.__proto__;
        TAJS_promisify(p);
        if (then === undefined || typeof then !== 'function') {
            TAJS_resolve(p, val);
        } else {
            f(function (val) {
                TAJS_resolve(p, val);
            },
            function (val) {
                TAJS_reject(p, val);
            });
        }
        return p;
    };

    global.Promise.reject = function (val) {
        var p = new _Promise();
        p.__proto__ = this.__proto__;
        TAJS_promisify(p);
        TAJS_reject(p, val);
        return p;
    };

    global.Promise.prototype.then = function(onFulfilled, onRejected) {
        var p = new _Promise();
        p.__proto__ = this.__proto__;
        TAJS_promisify(p);
        TAJS_onResolve(this, function(val) {
            try {
                var res = onFulfilled(val);
                TAJS_resolve(p, res);
            } catch (e) {
                TAJS_reject(p, e);
            }
        });
        TAJS_onReject(this, function(val) {
            try {
                var res = onRejected(val);
                TAJS_resolve(p, res);
            } catch (e) {
                TAJS_reject(p, e);
            }
        });
        return p;
    };

    global.Promise.prototype.catch = function (onRejected) {
        return this.then(undefined, onRejected);
    };

    global.Promise.prototype.finally = function (handler) {
        return this.then(handler, handler);
    };

    global.setTimeout = function (f) {
        TAJS_asyncListen(f);
        return TAJS_join(UInt, {/* nodejs object */});
    };
    global.setInterval = function (f, t) {
        TAJS_asyncListen(f);
        return TAJS_join(UInt, {/* nodejs object */});
    };
    global.clearInterval = function (id) {
        // NOOP
    };
    global.clearTimeout = function (id) {
        // NOOP
    };
})(this);