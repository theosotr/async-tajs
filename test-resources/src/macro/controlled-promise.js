"use strict";

var _createClass = (function() {
    function defineProperties(target, props) {
        for (var i = 0; i < props.length; i++) {
            var descriptor = props[i];
            descriptor.enumerable = descriptor.enumerable || false;
            descriptor.configurable = true;
            if ("value" in descriptor) descriptor.writable = true;
            Object.defineProperty(target, descriptor.key, descriptor);
        }
    }
    return function(Constructor, protoProps, staticProps) {
        if (protoProps) defineProperties(Constructor.prototype, protoProps);
        if (staticProps) defineProperties(Constructor, staticProps);
        return Constructor;
    };
})();

function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
        throw new TypeError("Cannot call a class as a function");
    }
}

function clearTimeout() {
    
}

/**
 * Creates controlled promise. In contrast to original Promise, it does not immediately call any function.
 * Instead it has [.call()](#ControlledPromise+call) method for that and `resolve / reject` methods for
 * resolving promise.
 */
function ControlledPromise() {
    this._resolve = null;
    this._reject = null;
    this._isPending = false;
    this._isFulfilled = false;
    this._isRejected = false;
    this._value = undefined;
    this._promise = null;
    this._timer = null;
    this._timeout = 0;
    this._timeoutReason = "Promise rejected by timeout";
    this._resetReason = "Promise rejected by reset";
}

/**
 * This method executes `fn` and returns promise. While promise is pending all subsequent calls of `.call(fn)`
 * will return the same promise. To fulfill that promise you can use `.resolve() / .reject()` methods.
 *
 * @param {Function} fn
 * @returns {Promise}
 */
ControlledPromise.prototype.call = function call(fn) {
    if (!this._isPending) {
        this.reset();
        this._createPromise();
        this._createTimer();
        this._callFn(fn);
    }
    return this._promise;
};

/**
 * Resolves pending promise with specified `value`.
 *
 * @param {*} [value]
 */

ControlledPromise.prototype.resolve = function resolve(value) {
    if (this._isPending) {
        if (isPromise(value)) {
            this._tryAttachToPromise(value);
        } else {
            this._settle(value);
            this._isFulfilled = true;
            this._resolve(value);
        }
    }
};


/**
 * Rejects pending promise with specified `value`.
 *
 * @param {*} [value]
 */

ControlledPromise.prototype.reject = function reject(value) {
    if (this._isPending) {
        this._settle(value);
        this._isRejected = true;
        this._reject(value);
    }
};

/**
 * Resets to initial state.
 */

ControlledPromise.prototype.reset = function reset() {
    if (this._isPending) {
        this.reject(new Error(this._resetReason));
    }
    this._promise = null;
    this._isPending = false;
    this._isFulfilled = false;
    this._isRejected = false;
    this._value = undefined;
    this._clearTimer();
};


/**
 * Sets timeout to reject promise automatically.
 *
 * @param {Number} ms delay in ms after that promise will be rejected automatically
 * @param {String|Error|Function} [reason] rejection value. If it is string or error - promise will be rejected with
 * that error. If it is function - this function will be called after delay where you can manually resolve or reject
 * promise via `.resolve() / .reject()` methods.
 */

ControlledPromise.prototype.timeout = function timeout(ms, reason) {
    this._timeout = ms;
    if (reason !== undefined) {
        this._timeoutReason = reason;
    }
};

ControlledPromise.prototype._createPromise = function _createPromise() {
    var _this = this;

    this._promise = new Promise(function F(resolve, reject) {
        _this._isPending = true;
        _this._resolve = resolve;
        _this._reject = reject;
    });
};


ControlledPromise.prototype._handleTimeout = function _handleTimeout() {
    if (typeof this._timeoutReason === "function") {
        this._timeoutReason();
    } else {
        var error =
            typeof this._timeoutReason === "string"
                ? new Error(this._timeoutReason)
                : this._timeoutReason;
        this.reject(error);
    }
};


ControlledPromise.prototype._createTimer = function _createTimer() {
    var _this2 = this;

    if (this._timeout) {
        this._timer = setTimeout(function() {
            return _this2._handleTimeout();
        }, this._timeout);
    }
};


ControlledPromise.prototype._clearTimer = function _clearTimer() {
    if (this._timer) {
        clearTimeout(this._timer);
        this._timer = null;
    }
};


ControlledPromise.prototype._settle = function _settle(value) {
    this._isPending = false;
    this._value = value;
    this._clearTimer();
};


ControlledPromise.prototype._callFn = function _callFn(fn) {
    if (typeof fn === "function") {
        try {
            var result = fn();
            this._tryAttachToPromise(result);
        } catch (e) {
            this.reject(e);
        }
    }
};


ControlledPromise.prototype._tryAttachToPromise = function _tryAttachToPromise(
    p
) {
    var _this3 = this;

    if (isPromise(p)) {
        p.then(
            function(value) {
                return _this3.resolve(value);
            },
            function(e) {
                return _this3.reject(e);
            }
        );
    }
};


/**
 * Returns true if promise is pending.
 *
 * @returns {Boolean}
 */
ControlledPromise.prototype.isPending = function() {
    return this._isPending;
};


/**
 * Returns true if promise is fulfilled.
 *
 * @returns {Boolean}
 */
ControlledPromise.prototype.isFulfilled = function () {
    return this._isFulfilled;
};


/**
 * Returns true if promise rejected.
 *
 * @returns {Boolean}
 */
ControlledPromise.prototype.isRejected = function () {
    return this._isRejected;
};


/**
 * Returns true if promise fulfilled or rejected.
 *
 * @returns {Boolean}
 */
ControlledPromise.prototype.isSettled = function () {
    return this._isFulfilled || this._isRejected;
};


/**
 * Returns true if promise already called via `.call()` method.
 *
 * @returns {Boolean}
 */
ControlledPromise.prototype.isCalled = function () {
    return this.isPending || this.isSettled;
};


function isPromise(p) {
    TAJS_makeContextSensitive(isPromise, 0);
    return p && typeof p.then === "function";
}


function bar() {
    TAJS_dumpValue('bar');
}

function ff(c) {
    TAJS_dumpValue(c);
}

var z;
function ff1(value) {
    z = {foo : value};
    TAJS_dumpValue(value);
    return value;
}

function test1() {
    var c = new ControlledPromise();
    c.timeout(1, function f() {
        c.reject(z.foo);
    });
    var p = c.call(function () {
        bar();
    });
    p.catch(ff);
}

function test2() {
    var c = new ControlledPromise();
    var p = c.call(function () {
        bar();
    });
    p.then(ff);
    TAJS_dumpValue(c);
    var p2 = Promise.resolve(c).then(ff1);
    c.resolve(p2);
    c.isCalled();
    c.isRejected();
    c.isFulfilled();
    c.isPending();
}


test1();
test2();