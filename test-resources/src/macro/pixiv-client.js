/* eslint strict:0 */

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



"use strict";
// STUBS
function XMLHttpRequest() {}

XMLHttpRequest.prototype.upload = new XMLHttpRequest();

XMLHttpRequest.prototype.onreadystatechange = function defaultXMLOnload() {};

XMLHttpRequest.prototype.onerror = function defaultOnError() {};

XMLHttpRequest.prototype.send = function() {
    this.responseURL = TAJS_make("AnyStr");
    this.responseText = TAJS_make("AnyStr");
    this.status = 200;
    this.statusText = TAJS_make('AnyStr');
    this.readyState = 4;
    TAJS_addAsyncIOCallback(this.onreadystatechange);
};

XMLHttpRequest.prototype.open = function(method) {};

XMLHttpRequest.prototype.abort = function() {};

XMLHttpRequest.prototype.getAllResponseHeaders = function() {
    return "";
};

XMLHttpRequest.prototype.setRequestHeader = function(header) {};

XMLHttpRequest.prototype.addEventListener = function (event, clb) {
    TAJS_makeContextSensitive(this.addEventListener, 0);
    TAJS_makeContextSensitive(this.addEventListener, 1);
    TAJS_addAsyncIOCallback(clb);
};

function btoa(str) {
    return TAJS_make('AnyStr');
}

function Blob() { }
function FormData() { }
function ArrayBuffer() { }
function DataView() {

}
var exports = {};
self = this;
window = this;
document = this;



// PROGRAM BEGINS
var _typeof =
    typeof Symbol === "function" && typeof Symbol.iterator === "symbol"
        ? function(obj) {
            return typeof obj;
        }
        : function(obj) {
            return obj &&
            typeof Symbol === "function" &&
            obj.constructor === Symbol &&
            obj !== Symbol.prototype
                ? "symbol"
                : typeof obj;
        };


function isBuffer(obj) {
    return (
        obj != null &&
        obj.constructor != null &&
        typeof obj.constructor.isBuffer === "function" &&
        obj.constructor.isBuffer(obj)
    );
}

function bind(fn, thisArg) {
    TAJS_makeContextSensitive(bind, 0);
    TAJS_makeContextSensitive(bind, 1);
    return function wrap() {
        var args = new Array(arguments.length);
        for (var i = 0; i < args.length; i++) {
            args[i] = arguments[i];
        }
        return fn.apply(thisArg, args);
    };
}

function encode(val) {
    return encodeURIComponent(val)
        .replace(/%40/gi, "@")
        .replace(/%3A/gi, ":")
        .replace(/%24/g, "$")
        .replace(/%2C/gi, ",")
        .replace(/%20/g, "+")
        .replace(/%5B/gi, "[")
        .replace(/%5D/gi, "]");
}

/*global toString:true*/

// utils is a library of generic helper functions non-specific to axios

var toString = Object.prototype.toString;

/**
 * Determine if a value is an Array
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Array, otherwise false
 */
function isArray(val) {
    return toString.call(val) === "[object Array]";
}

/**
 * Determine if a value is an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an ArrayBuffer, otherwise false
 */
function isArrayBuffer(val) {
    return toString.call(val) === "[object ArrayBuffer]";
}

/**
 * Determine if a value is a FormData
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an FormData, otherwise false
 */
function isFormData(val) {
    return typeof FormData !== "undefined" && val instanceof FormData;
}

/**
 * Determine if a value is a view on an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a view on an ArrayBuffer, otherwise false
 */
function isArrayBufferView(val) {
    var result;
    if (typeof ArrayBuffer !== "undefined" && ArrayBuffer.isView) {
        result = ArrayBuffer.isView(val);
    } else {
        result = val && val.buffer && val.buffer instanceof ArrayBuffer;
    }
    return result;
}

/**
 * Determine if a value is a String
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a String, otherwise false
 */
function isString(val) {
    return typeof val === "string";
}

/**
 * Determine if a value is a Number
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Number, otherwise false
 */
function isNumber(val) {
    return typeof val === "number";
}

/**
 * Determine if a value is undefined
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if the value is undefined, otherwise false
 */
function isUndefined(val) {
    return typeof val === "undefined";
}

/**
 * Determine if a value is an Object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Object, otherwise false
 */
function isObject(val) {
    return (
        val !== null &&
        (typeof val === "undefined" ? "undefined" : _typeof(val)) === "object"
    );
}

/**
 * Determine if a value is a Date
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Date, otherwise false
 */
function isDate(val) {
    return toString.call(val) === "[object Date]";
}

/**
 * Determine if a value is a File
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a File, otherwise false
 */
function isFile(val) {
    return toString.call(val) === "[object File]";
}

/**
 * Determine if a value is a Blob
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Blob, otherwise false
 */
function isBlob(val) {
    return toString.call(val) === "[object Blob]";
}

/**
 * Determine if a value is a Function
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Function, otherwise false
 */
function isFunction(val) {
    return toString.call(val) === "[object Function]";
}

/**
 * Determine if a value is a Stream
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Stream, otherwise false
 */
function isStream(val) {
    return isObject(val) && isFunction(val.pipe);
}

/**
 * Determine if a value is a URLSearchParams object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a URLSearchParams object, otherwise false
 */
function isURLSearchParams(val) {
    return (
        typeof URLSearchParams !== "undefined" && val instanceof URLSearchParams
    );
}

/**
 * Trim excess whitespace off the beginning and end of a string
 *
 * @param {String} str The String to trim
 * @returns {String} The String freed of excess whitespace
 */
function trim(str) {
    return str.replace(/^\s*/, "").replace(/\s*$/, "");
}

/**
 * Determine if we're running in a standard browser environment
 *
 * This allows axios to run in a web worker, and react-native.
 * Both environments support XMLHttpRequest, but not fully standard globals.
 *
 * web workers:
 *  typeof window -> undefined
 *  typeof document -> undefined
 *
 * react-native:
 *  navigator.product -> 'ReactNative'
 * nativescript
 *  navigator.product -> 'NativeScript' or 'NS'
 */
function isStandardBrowserEnv() {
    if (
        typeof navigator !== "undefined" &&
        (navigator.product === "ReactNative" ||
            navigator.product === "NativeScript" ||
            navigator.product === "NS")
    ) {
        return false;
    }
    return typeof window !== "undefined" && typeof document !== "undefined";
}

/**
 * Iterate over an Array or an Object invoking a function for each item.
 *
 * If `obj` is an Array callback will be called passing
 * the value, index, and complete array for each item.
 *
 * If 'obj' is an Object callback will be called passing
 * the value, key, and complete object for each property.
 *
 * @param {Object|Array} obj The object to iterate
 * @param {Function} fn The callback to invoke for each item
 */
function forEach(obj, fn) {
    TAJS_makeContextSensitive(forEach, 0);
    TAJS_makeContextSensitive(forEach, 1);
    TAJS_dumpValue('forEach executed');
    // Don't bother if no value provided
    if (obj === null || typeof obj === "undefined") {
        return;
    }

    // Force an array if not already something iterable
    if ((typeof obj === "undefined" ? "undefined" : _typeof(obj)) !== "object") {
        /*eslint no-param-reassign:0*/
        obj = [obj];
    }

    if (isArray(obj)) {
        // Iterate over array values
        for (var i = 0, l = obj.length; i < l; i++) {
            fn(obj[i], i, obj);
        }
    } else {
        // Iterate over object keys
        for (var key in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, key)) {
                fn(obj[key], key, obj);
            }
        }
    }
}

/**
 * Accepts varargs expecting each argument to be an object, then
 * immutably merges the properties of each object and returns result.
 *
 * When multiple objects contain the same key the later object in
 * the arguments list will take precedence.
 *
 * Example:
 *
 * ```js
 * var result = merge({foo: 123}, {foo: 456});
 * console.log(result.foo); // outputs 456
 * ```
 *
 * @param {Object} obj1 Object to merge
 * @returns {Object} Result of all merge properties
 */
function merge() /* obj1, obj2, obj3, ... */ {
    var result = {};
    function assignValue(val, key) {
        TAJS_makeContextSensitive(assignValue, 0);
        TAJS_makeContextSensitive(assignValue, 1);
        if (
            _typeof(result[key]) === "object" &&
            (typeof val === "undefined" ? "undefined" : _typeof(val)) === "object"
        ) {
            result[key] = merge(result[key], val);
        } else {
            result[key] = val;
        }
    }

    for (var i = 0, l = arguments.length; i < l; i++) {
        forEach(arguments[i], assignValue);
    }
    return result;
}

/**
 * Function equal to merge with the difference being that no reference
 * to original objects is kept.
 *
 * @see merge
 * @param {Object} obj1 Object to merge
 * @returns {Object} Result of all merge properties
 */
function deepMerge() /* obj1, obj2, obj3, ... */ {
    var result = {};
    function assignValue(val, key) {
        if (
            _typeof(result[key]) === "object" &&
            (typeof val === "undefined" ? "undefined" : _typeof(val)) === "object"
        ) {
            result[key] = deepMerge(result[key], val);
        } else if (
            (typeof val === "undefined" ? "undefined" : _typeof(val)) === "object"
        ) {
            result[key] = deepMerge({}, val);
        } else {
            result[key] = val;
        }
    }

    for (var i = 0, l = arguments.length; i < l; i++) {
        forEach(arguments[i], assignValue);
    }
    return result;
}

/**
 * Extends object a by mutably adding to it the properties of object b.
 *
 * @param {Object} a The object to be extended
 * @param {Object} b The object to copy properties from
 * @param {Object} thisArg The object to bind function to
 * @return {Object} The resulting value of object a
 */
function extend(a, b, thisArg) {
    forEach(b, function assignValue(val, key) {
        if (thisArg && typeof val === "function") {
            a[key] = bind(val, thisArg);
        } else {
            a[key] = val;
        }
    });
    return a;
}

var utils = {
    isArray: isArray,
    isArrayBuffer: isArrayBuffer,
    isBuffer: isBuffer,
    isFormData: isFormData,
    isArrayBufferView: isArrayBufferView,
    isString: isString,
    isNumber: isNumber,
    isObject: isObject,
    isUndefined: isUndefined,
    isDate: isDate,
    isFile: isFile,
    isBlob: isBlob,
    isFunction: isFunction,
    isStream: isStream,
    isURLSearchParams: isURLSearchParams,
    isStandardBrowserEnv: isStandardBrowserEnv,
    forEach: forEach,
    merge: merge,
    deepMerge: deepMerge,
    extend: extend,
    trim: trim
};

function xhrAdapter(config) {
    return new Promise(function dispatchXhrRequest(resolve, reject) {
        TAJS_dumpValue(config);
        var requestData = config.data;
        var requestHeaders = config.headers;

        if (utils.isFormData(requestData)) {
            delete requestHeaders["Content-Type"]; // Let the browser set it
        }

        var request = new XMLHttpRequest();

        // HTTP basic authentication
        if (config.auth) {
            var username = config.auth.username || "";
            var password = config.auth.password || "";
            requestHeaders.Authorization = "Basic " + btoa(username + ":" + password);
        }

        request.open(
            config.method.toUpperCase(),
            buildURL(config.url, config.params, config.paramsSerializer),
            true
        );

        // Set the request timeout in MS
        request.timeout = config.timeout;

        // Listen for ready state
        request.onreadystatechange = function () {
            if (!request || request.readyState !== 4) {
                return;
            }

            // The request errored out and we didn't get a response, this will be
            // handled by onerror instead
            // With one exception: request that using file: protocol, most browsers
            // will return status as 0 even though it's a successful request
            if (
                request.status === 0 &&
                !(request.responseURL && request.responseURL.indexOf("file:") === 0)
            ) {
                return;
            }

            // Prepare the response
            var responseHeaders =
                "getAllResponseHeaders" in request
                    ? parseHeaders(request.getAllResponseHeaders())
                    : null;
            var responseData =
                !config.responseType || config.responseType === "text"
                    ? request.responseText
                    : request.response;
            var response = {
                data: responseData,
                status: request.status,
                statusText: request.statusText,
                headers: responseHeaders,
                config: config,
                request: request
            };

            settle(resolve, reject, response);

            // Clean up request
            request = null;
        };

        // Handle browser request cancellation (as opposed to a manual cancellation)
        request.onabort = function handleAbort() {
            if (!request) {
                return;
            }

            reject(createError("Request aborted", config, "ECONNABORTED", request));

            // Clean up request
            request = null;
        };

        // Handle low level network errors
        request.onerror = function handleError() {
            // Real errors are hidden from us by the browser
            // onerror should only fire if it's a network error
            reject(createError("Network Error", config, null, request));

            // Clean up request
            request = null;
        };

        // Handle timeout
        request.ontimeout = function handleTimeout() {
            reject(
                createError(
                    "timeout of " + config.timeout + "ms exceeded",
                    config,
                    "ECONNABORTED",
                    request
                )
            );

            // Clean up request
            request = null;
        };

        // Add xsrf header
        // This is only done if running in a standard browser environment.
        // Specifically not if we're in a web worker, or react-native.
        if (utils.isStandardBrowserEnv()) {
            TAJS_dumpValue('cookies');

            // Add xsrf header
            var xsrfValue =
                (config.withCredentials || isURLSameOrigin(config.url)) &&
                config.xsrfCookieName
                    ? cookies.read(config.xsrfCookieName)
                    : undefined;

            if (xsrfValue) {
                requestHeaders[config.xsrfHeaderName] = xsrfValue;
            }
        }

        // Add headers to the request
        if ("setRequestHeader" in request) {
            utils.forEach(requestHeaders, function setRequestHeader(val, key) {
                TAJS_makeContextSensitive(setRequestHeader, -2);
                if (
                    typeof requestData === "undefined" &&
                    key.toLowerCase() === "content-type"
                ) {
                    // Remove Content-Type if data is undefined
                    delete requestHeaders[key];
                } else {
                    // Otherwise add header to the request
                    request.setRequestHeader(key, val);
                }
            });
        }

        // Add withCredentials to request if needed
        if (config.withCredentials) {
            request.withCredentials = true;
        }

        // Add responseType to request if needed
        if (config.responseType) {
            try {
                request.responseType = config.responseType;
            } catch (e) {
                // Expected DOMException thrown by browsers not compatible XMLHttpRequest Level 2.
                // But, this can be suppressed for 'json' type as it can be parsed by default 'transformResponse' function.
                if (config.responseType !== "json") {
                    throw e;
                }
            }
        }

        // Handle progress if needed
        if (typeof config.onDownloadProgress === "function") {
            request.addEventListener("progress", config.onDownloadProgress);
        }

        // Not all browsers support upload events
        if (typeof config.onUploadProgress === "function" && request.upload) {
            request.upload.addEventListener("progress", config.onUploadProgress);
        }

        if (config.cancelToken) {
            // Handle cancellation
            config.cancelToken.promise.then(function onCanceled(cancel) {
                if (!request) {
                    return;
                }

                request.abort();
                reject(cancel);
                // Clean up request
                request = null;
            });
        }

        if (requestData === undefined) {
            requestData = null;
        }
        // Send the request
        request.send(requestData);
    });
}

function buildURL(url, params, paramsSerializer) {
    /*eslint no-param-reassign:0*/
    if (!params) {
        return url;
    }

    var serializedParams;
    if (paramsSerializer) {
        serializedParams = paramsSerializer(params);
    } else if (utils.isURLSearchParams(params)) {
        serializedParams = params.toString();
    } else {
        var parts = [];

        utils.forEach(params, function serialize(val, key) {
            if (val === null || typeof val === "undefined") {
                return;
            }

            if (utils.isArray(val)) {
                key = key + "[]";
            } else {
                val = [val];
            }

            utils.forEach(val, function parseValue(v) {
                if (utils.isDate(v)) {
                    v = v.toISOString();
                } else if (utils.isObject(v)) {
                    v = JSON.stringify(v);
                }
                parts.push(encode(key) + "=" + encode(v));
            });
        });

        serializedParams = parts.join("&");
    }

    if (serializedParams) {
        url += (url.indexOf("?") === -1 ? "?" : "&") + serializedParams;
    }

    return url;
}

function combineURLs(baseURL, relativeURL) {
    return relativeURL
        ? baseURL.replace(/\/+$/, "") + "/" + relativeURL.replace(/^\/+/, "")
        : baseURL;
}

var cookies = {
    write: function write() {},
    read: function read() {
        return null;
    },
    remove: function remove() {}
};

function deprecatedMethod(method, instead, docs) {
    try {
        console.warn(
            "DEPRECATED method `" +
            method +
            "`." +
            (instead ? " Use `" + instead + "` instead." : "") +
            " This method will be removed in a future release."
        );

        if (docs) {
            console.warn("For more information about usage see " + docs);
        }
    } catch (e) {
        /* Ignore */
    }
}

/**
 * Determines whether the specified URL is absolute
 *
 * @param {string} url The URL to test
 * @returns {boolean} True if the specified URL is absolute, otherwise false
 */
function isAbsoluteURL(url) {
    // A URL is considered absolute if it begins with "<scheme>://" or "//" (protocol-relative URL).
    // RFC 3986 defines scheme name as a sequence of characters beginning with a letter and followed
    // by any combination of letters, digits, plus, period, or hyphen.
    return /^([a-z][a-z\d\+\-\.]*:)?\/\//i.test(url);
}

function isURLSameOrigin() {
    return true;
}

function normalizeHeaderName(headers, normalizedName) {
    TAJS_makeContextSensitive(normalizeHeaderName, 0);
    TAJS_makeContextSensitive(normalizeHeaderName, 1);
    utils.forEach(headers, function processHeader(value, name) {
        if (
            name !== normalizedName &&
            name.toUpperCase() === normalizedName.toUpperCase()
        ) {
            headers[normalizedName] = value;
            delete headers[name];
        }
    });
}

var ignoreDuplicateOf = [
    "age",
    "authorization",
    "content-length",
    "content-type",
    "etag",
    "expires",
    "from",
    "host",
    "if-modified-since",
    "if-unmodified-since",
    "last-modified",
    "location",
    "max-forwards",
    "proxy-authorization",
    "referer",
    "retry-after",
    "user-agent"
];

/**
 * Parse headers into an object
 *
 * ```
 * Date: Wed, 27 Aug 2014 08:58:49 GMT
 * Content-Type: application/json
 * Connection: keep-alive
 * Transfer-Encoding: chunked
 * ```
 *
 * @param {String} headers Headers needing to be parsed
 * @returns {Object} Headers parsed into an object
 */
function parseHeaders(headers) {
    TAJS_makeContextSensitive(parseHeaders, 0);
    var parsed = {};
    var key;
    var val;
    var i;

    if (!headers) {
        return parsed;
    }

    utils.forEach(headers.split("\n"), function parser(line) {
        i = line.indexOf(":");
        key = utils.trim(line.substr(0, i)).toLowerCase();
        val = utils.trim(line.substr(i + 1));

        if (key) {
            if (parsed[key] && ignoreDuplicateOf.indexOf(key) >= 0) {
                return;
            }
            if (key === "set-cookie") {
                parsed[key] = (parsed[key] ? parsed[key] : []).concat([val]);
            } else {
                parsed[key] = parsed[key] ? parsed[key] + ", " + val : val;
            }
        }
    });

    return parsed;
}

/**
 * Syntactic sugar for invoking a function and expanding an array for arguments.
 *
 * Common use case would be to use `Function.prototype.apply`.
 *
 *  ```js
 *  function f(x, y, z) {}
 *  var args = [1, 2, 3];
 *  f.apply(null, args);
 *  ```
 *
 * With `spread` this example can be re-written.
 *
 *  ```js
 *  spread(function(x, y, z) {})([1, 2, 3]);
 *  ```
 *
 * @param {Function} callback
 * @returns {Function}
 */
function spread(callback) {
    return function wrap(arr) {
        return callback(arr);
    };
}

/**
 * A `Cancel` is an object that is thrown when an operation is canceled.
 *
 * @class
 * @param {string=} message The message.
 */
function Cancel(message) {
    this.message = message;
}

Cancel.prototype.toString = function toString() {
    return "Cancel" + (this.message ? ": " + this.message : "");
};

Cancel.prototype.__CANCEL__ = true;

/**
 * A `CancelToken` is an object that can be used to request cancellation of an operation.
 *
 * @class
 * @param {Function} executor The executor function.
 */
function CancelToken(executor) {
    if (typeof executor !== "function") {
        throw new TypeError("executor must be a function.");
    }

    var resolvePromise;
    this.promise = new Promise(function promiseExecutor(resolve) {
        resolvePromise = resolve;
    });

    var token = this;
    executor(function cancel(message) {
        if (token.reason) {
            // Cancellation has already been requested
            return;
        }

        token.reason = new Cancel(message);
        resolvePromise(token.reason);
    });
}

/**
 * Throws a `Cancel` if cancellation has been requested.
 */
CancelToken.prototype.throwIfRequested = function throwIfRequested() {
    if (this.reason) {
        throw this.reason;
    }
};

/**
 * Returns an object that contains a new `CancelToken` and a function that, when called,
 * cancels the `CancelToken`.
 */
CancelToken.source = function source() {
    var cancel;
    var token = new CancelToken(function executor(c) {
        cancel = c;
    });
    return {
        token: token,
        cancel: cancel
    };
};

function isCancel(value) {
    return !!(value && value.__CANCEL__);
}

function InterceptorManager() {
    this.handlers = [];
}

/**
 * Add a new interceptor to the stack
 *
 * @param {Function} fulfilled The function to handle `then` for a `Promise`
 * @param {Function} rejected The function to handle `reject` for a `Promise`
 *
 * @return {Number} An ID used to remove interceptor later
 */
InterceptorManager.prototype.use = function use(fulfilled, rejected) {
    TAJS_makeContextSensitive(this.use, 0);
    TAJS_makeContextSensitive(this.use, 1);
    this.handlers.push({
        fulfilled: fulfilled,
        rejected: rejected
    });
    return this.handlers.length - 1;
};

/**
 * Remove an interceptor from the stack
 *
 * @param {Number} id The ID that was returned by `use`
 */
InterceptorManager.prototype.eject = function eject(id) {
    if (this.handlers[id]) {
        this.handlers[id] = null;
    }
};

/**
 * Iterate over all the registered interceptors
 *
 * This method is particularly useful for skipping over any
 * interceptors that may have become `null` calling `eject`.
 *
 * @param {Function} fn The function to call for each interceptor
 */
InterceptorManager.prototype.forEach = function forEach(fn) {
    TAJS_makeContextSensitive(this.forEach, 0);
    TAJS_makeContextSensitive(this.forEach, -1);
    utils.forEach(this.handlers, function forEachHandler(h) {
        if (h !== null) {
            fn(h);
        }
    });
};

/**
 * Update an Error with the specified config, error code, and response.
 *
 * @param {Error} error The error to update.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 * @param {Object} [request] The request.
 * @param {Object} [response] The response.
 * @returns {Error} The error.
 */
function enhanceError(error, config, code, request, response) {
    error.config = config;
    if (code) {
        error.code = code;
    }
    error.request = request;
    error.response = response;
    error.toJSON = function() {
        return {
            // Standard
            message: this.message,
            name: this.name,
            // Microsoft
            description: this.description,
            number: this.number,
            // Mozilla
            fileName: this.fileName,
            lineNumber: this.lineNumber,
            columnNumber: this.columnNumber,
            stack: this.stack,
            // Axios
            config: this.config,
            code: this.code
        };
    };
    return error;
}

/**
 * Create an Error with the specified message, config, error code, request and response.
 *
 * @param {string} message The error message.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 * @param {Object} [request] The request.
 * @param {Object} [response] The response.
 * @returns {Error} The created error.
 */
function createError(message, config, code, request, response) {
    var error = new Error(message);
    return enhanceError(error, config, code, request, response);
}

/**
 * Config-specific merge-function which creates a new config-object
 * by merging two configuration objects together.
 *
 * @param {Object} config1
 * @param {Object} config2
 * @returns {Object} New object resulting from merging config2 to config1
 */
function mergeConfig(config1, config2) {
    // eslint-disable-next-line no-param-reassign
    config2 = config2 || {};
    var config = {};

    utils.forEach(["url", "method", "params", "data"], function valueFromConfig2(
        prop
    ) {
        if (typeof config2[prop] !== "undefined") {
            config[prop] = config2[prop];
        }
    });

    utils.forEach(["headers", "auth", "proxy"], function mergeDeepProperties(
        prop
    ) {
        if (utils.isObject(config2[prop])) {
            config[prop] = utils.deepMerge(config1[prop], config2[prop]);
        } else if (typeof config2[prop] !== "undefined") {
            config[prop] = config2[prop];
        } else if (utils.isObject(config1[prop])) {
            config[prop] = utils.deepMerge(config1[prop]);
        } else if (typeof config1[prop] !== "undefined") {
            config[prop] = config1[prop];
        }
    });

    utils.forEach(
        [
            "baseURL",
            "transformRequest",
            "transformResponse",
            "paramsSerializer",
            "timeout",
            "withCredentials",
            "adapter",
            "responseType",
            "xsrfCookieName",
            "xsrfHeaderName",
            "onUploadProgress",
            "onDownloadProgress",
            "maxContentLength",
            "validateStatus",
            "maxRedirects",
            "httpAgent",
            "httpsAgent",
            "cancelToken",
            "socketPath"
        ],
        function defaultToConfig2(prop) {
            if (typeof config2[prop] !== "undefined") {
                config[prop] = config2[prop];
            } else if (typeof config1[prop] !== "undefined") {
                config[prop] = config1[prop];
            }
        }
    );

    return config;
}

/**
 * Create a new instance of Axios
 *
 * @param {Object} instanceConfig The default config for the instance
 */
function Axios(instanceConfig) {
    this.defaults = instanceConfig;
    this.interceptors = {
        request: new InterceptorManager(),
        response: new InterceptorManager()
    };
}

/**
 * Dispatch a request
 *
 * @param {Object} config The config specific for this request (merged with this.defaults)
 */
Axios.prototype.request = function request(config) {
    TAJS_makeContextSensitive(this.request, -1);
    TAJS_makeContextSensitive(this.request, 0);
    TAJS_dumpValue(config);

    /*eslint no-param-reassign:0*/
    // Allow for axios('example/url'[, config]) a la fetch API
    //if (typeof config === "string") {
    //    config = arguments[1] || {};
    //    config.url = arguments[0];
    //} else {
    //    config = config || {};
    //}

    //config = mergeConfig(this.defaults, config);
    config.method = config.method ? config.method.toLowerCase() : "get";

    // Hook up interceptors middleware
    var chain = [dispatchRequest, undefined];
    var promise = Promise.resolve(config);

    this.interceptors.request.forEach(function unshiftRequestInterceptors(
        interceptor
    ) {
        chain.unshift(interceptor.fulfilled, interceptor.rejected);
    });

    this.interceptors.response.forEach(function pushResponseInterceptors(
        interceptor
    ) {
        chain.push(interceptor.fulfilled, interceptor.rejected);
    });

    while (chain.length) {
        promise = promise.then(chain.shift(), chain.shift());
    }

    return promise;
};

Axios.prototype.getUri = function getUri(config) {
    //config = mergeConfig(this.defaults, config);
    return buildURL(config.url, config.params, config.paramsSerializer).replace(
        /^\?/,
        ""
    );
};

// Provide aliases for supported request methods
function injectMethods() {
    utils.forEach(
        ["delete", "get", "head", "options"],
        function forEachMethodNoData(method) {
            /*eslint func-names:0*/
            Axios.prototype[method] = function (url, config) {
                return this.request(
                    utils.merge(config || {}, {
                        method: method,
                        url: url
                    })
                );
            };
        }
    );

    utils.forEach(["post", "put", "patch"], function forEachMethodWithData(method) {
        /*eslint func-names:0*/
        Axios.prototype[method] = function (url, data, config) {
            return this.request(
                utils.merge(config || {}, {
                    method: method,
                    url: url,
                    data: data
                })
            );
        };
    });
}


/**
 * Throws a `Cancel` if cancellation has been requested.
 */
function throwIfCancellationRequested(config) {
    if (config.cancelToken) {
        config.cancelToken.throwIfRequested();
    }
}

/**
 * Dispatch a request to the server using the configured adapter.
 *
 * @param {object} config The config that is to be used for the request
 * @returns {Promise} The Promise to be fulfilled
 */
function dispatchRequest(config) {
    TAJS_makeContextSensitive(dispatchRequest, 0);
    throwIfCancellationRequested(config);
    TAJS_dumpValue(config);

    // Support baseURL config
    if (config.baseURL && !isAbsoluteURL(config.url)) {
        config.url = combineURLs(config.baseURL, config.url);
    }

    // Ensure headers exist
    config.headers = config.headers || {};

    // Transform request data
    config.data = transformData(
        config.data,
        config.headers,
        config.transformRequest
    );

    // Flatten headers
    //config.headers = utils.merge(
    //    config.headers.common || {},
    //    config.headers[config.method] || {},
    //    config.headers || {}
    //);

    utils.forEach(
        ["delete", "get", "head", "post", "put", "patch", "common"],
        function cleanHeaderConfig(method) {
            TAJS_makeContextSensitive(cleanHeaderConfig, -2);
            delete config.headers[method];
        }
    );

    var adapter = config.adapter || defaults.adapter;
    return adapter(config).then(
        function onAdapterResolution(response) {
            TAJS_makeContextSensitive(onAdapterResolution, -2);
            throwIfCancellationRequested(config);

            // Transform response data
            response.data = transformData(
                response.data,
                response.headers,
                config.transformResponse
            );

            return response;
        },
        function onAdapterRejection(reason) {
            if (!isCancel(reason)) {
                throwIfCancellationRequested(config);

                // Transform response data
                if (reason && reason.response) {
                    reason.response.data = transformData(
                        reason.response.data,
                        reason.response.headers,
                        config.transformResponse
                    );
                }
            }

            return Promise.reject(reason);
        }
    );
}

/**
 * Resolve or reject a Promise based on response status.
 *
 * @param {Function} resolve A function that resolves the promise.
 * @param {Function} reject A function that rejects the promise.
 * @param {object} response The response.
 */
function settle(resolve, reject, response) {
    var validateStatus = response.config.validateStatus;
    if (!validateStatus || validateStatus(response.status)) {
        resolve(response);
    } else {
        reject(
            createError(
                "Request failed with status code " + response.status,
                response.config,
                null,
                response.request,
                response
            )
        );
    }
}

/**
 * Transform the data for a request or a response
 *
 * @param {Object|String} data The data to be transformed
 * @param {Array} headers The headers for the request or response
 * @param {Array|Function} fns A single function or Array of functions
 * @returns {*} The resulting transformed data
 */
function transformData(data, headers, fns) {
    TAJS_makeContextSensitive(transformData, 0);
    TAJS_makeContextSensitive(transformData, 2);
    /*eslint no-param-reassign:0*/
    utils.forEach(fns, function transform(fn) {
        data = fn(data, headers);
    });

    return data;
}

/** ---- DEFAULTS ----*/
var DEFAULT_CONTENT_TYPE = {
    "Content-Type": "application/x-www-form-urlencoded"
};

function setContentTypeIfUnset(headers, value) {
    if (
        !utils.isUndefined(headers) &&
        utils.isUndefined(headers["Content-Type"])
    ) {
        headers["Content-Type"] = value;
    }
}

function getDefaultAdapter() {
    return xhrAdapter;
}

var defaults = {
    adapter: getDefaultAdapter(),

    transformRequest: [
        function transformRequest(data, headers) {
            normalizeHeaderName(headers, "Accept");
            normalizeHeaderName(headers, "Content-Type");
            if (
                utils.isFormData(data) ||
                utils.isArrayBuffer(data) ||
                utils.isBuffer(data) ||
                utils.isStream(data) ||
                utils.isFile(data) ||
                utils.isBlob(data)
            ) {
                return data;
            }
            if (utils.isArrayBufferView(data)) {
                return data.buffer;
            }
            if (utils.isURLSearchParams(data)) {
                setContentTypeIfUnset(
                    headers,
                    "application/x-www-form-urlencoded;charset=utf-8"
                );
                return data.toString();
            }
            if (utils.isObject(data)) {
                setContentTypeIfUnset(headers, "application/json;charset=utf-8");
                return JSON.stringify(data);
            }
            return data;
        }
    ],

    transformResponse: [
        function transformResponse(data) {
            /*eslint no-param-reassign:0*/
            if (typeof data === "string") {
                try {
                    data = JSON.parse(data);
                } catch (e) {
                    /* Ignore */
                }
            }
            return data;
        }
    ],

    /**
     * A timeout in milliseconds to abort a request. If set to 0 (default) a
     * timeout is not created.
     */
    timeout: 0,

    xsrfCookieName: "XSRF-TOKEN",
    xsrfHeaderName: "X-XSRF-TOKEN",

    maxContentLength: -1,

    validateStatus: function validateStatus(status) {
        return status >= 200 && status < 300;
    }
};

defaults.headers = {
    common: {
        Accept: "application/json, text/plain, */*"
    }
};

function assignMethods() {
    utils.forEach(["delete", "get", "head"], function forEachMethodNoData(method) {
        defaults.headers[method] = {};
    });
}

utils.forEach(["post", "put", "patch"], function forEachMethodWithData(method) {
    defaults.headers[method] = utils.merge(DEFAULT_CONTENT_TYPE);
});

/**
 * Create an instance of Axios
 *
 * @param {Object} defaultConfig The default config for the instance
 * @return {Axios} A new instance of Axios
 */
function createInstance(defaultConfig) {
    return new Axios(defaultConfig);
}

// Create the default instance to be exported
var axios = createInstance(defaults);

// QS
var isBuffer2 = function isBuffer(obj) {
    if (obj === null || typeof obj === 'undefined') {
        return false;
    }

    return !!(obj.constructor && obj.constructor.isBuffer && obj.constructor.isBuffer(obj));
};

var assign = function assignSingleSource(target, source) {
    return Object.keys(source).reduce(function (acc, key) {
        acc[key] = source[key];
        return acc;
    }, target);
};

var decode = function (str, decoder, charset) {
    var strWithoutPlus = str.replace(/\+/g, ' ');
    if (charset === 'iso-8859-1') {
        // unescape never throws, no try...catch needed:
        return strWithoutPlus.replace(/%[0-9a-f]{2}/gi, unescape);
    }
    // utf-8
    try {
        return decodeURIComponent(strWithoutPlus);
    } catch (e) {
        return strWithoutPlus;
    }
};

var hexTable = (function () {
    var array = [];
    for (var i = 0; i < 256; ++i) {
        array.push('%' + ((i < 16 ? '0' : '') + i.toString(16)).toUpperCase());
    }

    return array;
}());


var encode = function encode(str, defaultEncoder, charset) {
    // This code was originally written by Brian White (mscdex) for the io.js core querystring library.
    // It has been adapted here for stricter adherence to RFC 3986
    if (str.length === 0) {
        return str;
    }

    var string = typeof str === 'string' ? str : String(str);

    if (charset === 'iso-8859-1') {
        return escape(string).replace(/%u[0-9a-f]{4}/gi, function ($0) {
            return '%26%23' + parseInt($0.slice(2), 16) + '%3B';
        });
    }

    var out = '';
    for (var i = 0; i < string.length; ++i) {
        var c = string.charCodeAt(i);

        if (
            c === 0x2D // -
            || c === 0x2E // .
            || c === 0x5F // _
            || c === 0x7E // ~
            || (c >= 0x30 && c <= 0x39) // 0-9
            || (c >= 0x41 && c <= 0x5A) // a-z
            || (c >= 0x61 && c <= 0x7A) // A-Z
        ) {
            out += string.charAt(i);
            continue;
        }

        if (c < 0x80) {
            out = out + hexTable[c];
            continue;
        }

        if (c < 0x800) {
            out = out + (hexTable[0xC0 | (c >> 6)] + hexTable[0x80 | (c & 0x3F)]);
            continue;
        }

        if (c < 0xD800 || c >= 0xE000) {
            out = out + (hexTable[0xE0 | (c >> 12)] + hexTable[0x80 | ((c >> 6) & 0x3F)] + hexTable[0x80 | (c & 0x3F)]);
            continue;
        }

        i += 1;
        c = 0x10000 + (((c & 0x3FF) << 10) | (string.charCodeAt(i) & 0x3FF));
        out += hexTable[0xF0 | (c >> 18)]
            + hexTable[0x80 | ((c >> 12) & 0x3F)]
            + hexTable[0x80 | ((c >> 6) & 0x3F)]
            + hexTable[0x80 | (c & 0x3F)];
    }

    return out;
};

var utils2 = {
    isBuffer: isBuffer2,
    encode: encode,

};
var replace = String.prototype.replace;
var percentTwenties = /%20/g;

var formats = {
    'default': 'RFC3986',
    formatters: {
        RFC1738: function (value) {
            return replace.call(value, percentTwenties, '+');
        },
        RFC3986: function (value) {
            return value;
        }
    },
    RFC1738: 'RFC1738',
    RFC3986: 'RFC3986'
};


var arrayPrefixGenerators = {
    brackets: function brackets(prefix) { // eslint-disable-line func-name-matching
        return prefix + '[]';
    },
    indices: function indices(prefix, key) { // eslint-disable-line func-name-matching
        return prefix + '[' + key + ']';
    },
    repeat: function repeat(prefix) { // eslint-disable-line func-name-matching
        return prefix;
    }
};

var toISO = Date.prototype.toISOString;

var defaults2 = {
    delimiter: '&',
    encode: true,
    encoder: utils2.encode,
    encodeValuesOnly: false,
    serializeDate: function serializeDate(date) { // eslint-disable-line func-name-matching
        return toISO.call(date);
    },
    skipNulls: false,
    strictNullHandling: false
};

var _stringify = function _stringify( // eslint-disable-line func-name-matching
    object,
    prefix,
    generateArrayPrefix,
    strictNullHandling,
    skipNulls,
    encoder,
    filter,
    sort,
    allowDots,
    serializeDate,
    formatter,
    encodeValuesOnly,
    charset
) {
    var obj = object;
    if (typeof filter === 'function') {
        obj = filter(prefix, obj);
    } else if (obj instanceof Date) {
        obj = serializeDate(obj);
    } else if (obj === null) {
        if (strictNullHandling) {
            return encoder && !encodeValuesOnly ? encoder(prefix, defaults2.encoder, charset) : prefix;
        }

        obj = '';
    }

    if (typeof obj === 'string' || typeof obj === 'number' || typeof obj === 'boolean' || utils2.isBuffer(obj)) {
        if (encoder) {
            var keyValue = encodeValuesOnly ? prefix : encoder(prefix, defaults2.encoder, charset);
            return [formatter(keyValue) + '=' + formatter(encoder(obj, defaults2.encoder, charset))];
        }
        return [formatter(prefix) + '=' + formatter(String(obj))];
    }

    var values = [];

    if (typeof obj === 'undefined') {
        return values;
    }

    var objKeys;
    if (Array.isArray(filter)) {
        objKeys = filter;
    } else {
        var keys = Object.keys(obj);
        objKeys = sort ? keys.sort(sort) : keys;
    }

    for (var i = 0; i < objKeys.length; ++i) {
        var key = objKeys[i];

        if (skipNulls && obj[key] === null) {
            continue;
        }

        if (Array.isArray(obj)) {
            values = values.concat(stringify(
                obj[key],
                generateArrayPrefix(prefix, key),
                generateArrayPrefix,
                strictNullHandling,
                skipNulls,
                encoder,
                filter,
                sort,
                allowDots,
                serializeDate,
                formatter,
                encodeValuesOnly,
                charset
            ));
        } else {
            values = values.concat(stringify(
                obj[key],
                prefix + (allowDots ? '.' + key : '[' + key + ']'),
                generateArrayPrefix,
                strictNullHandling,
                skipNulls,
                encoder,
                filter,
                sort,
                allowDots,
                serializeDate,
                formatter,
                encodeValuesOnly,
                charset
            ));
        }
    }

    return values;
};

function stringify(object, opts) {
    var obj = object;
    var options = opts ? assign({}, opts) : {};

    if (options.encoder !== null && options.encoder !== undefined && typeof options.encoder !== 'function') {
        throw new TypeError('Encoder has to be a function.');
    }

    var delimiter = typeof options.delimiter === 'undefined' ? defaults2.delimiter : options.delimiter;
    var strictNullHandling = typeof options.strictNullHandling === 'boolean' ? options.strictNullHandling : defaults2.strictNullHandling;
    var skipNulls = typeof options.skipNulls === 'boolean' ? options.skipNulls : defaults2.skipNulls;
    var encode = typeof options.encode === 'boolean' ? options.encode : defaults2.encode;
    var encoder = typeof options.encoder === 'function' ? options.encoder : defaults2.encoder;
    var sort = typeof options.sort === 'function' ? options.sort : null;
    var allowDots = typeof options.allowDots === 'undefined' ? false : options.allowDots;
    var serializeDate = typeof options.serializeDate === 'function' ? options.serializeDate : defaults2.serializeDate;
    var encodeValuesOnly = typeof options.encodeValuesOnly === 'boolean' ? options.encodeValuesOnly : defaults2.encodeValuesOnly;
    var charset = options.charset || 'utf-8';
    if (charset !== undefined && charset !== 'utf-8' && charset !== 'iso-8859-1') {
        throw new Error('The charset option must be either utf-8, iso-8859-1, or undefined');
    }

    if (typeof options.format === 'undefined') {
        options.format = formats['default'];
    } else if (!Object.prototype.hasOwnProperty.call(formats.formatters, options.format)) {
        throw new TypeError('Unknown format option provided.');
    }
    var formatter = formats.formatters[options.format];
    var objKeys;
    var filter;

    if (typeof options.filter === 'function') {
        filter = options.filter;
        obj = filter('', obj);
    } else if (Array.isArray(options.filter)) {
        filter = options.filter;
        objKeys = filter;
    }

    var keys = [];

    if (typeof obj !== 'object' || obj === null) {
        return '';
    }

    var arrayFormat;
    if (options.arrayFormat in arrayPrefixGenerators) {
        arrayFormat = options.arrayFormat;
    } else if ('indices' in options) {
        arrayFormat = options.indices ? 'indices' : 'repeat';
    } else {
        arrayFormat = 'indices';
    }

    var generateArrayPrefix = arrayPrefixGenerators[arrayFormat];

    if (!objKeys) {
        objKeys = Object.keys(obj);
    }

    if (sort) {
        objKeys.sort(sort);
    }

    for (var i = 0; i < objKeys.length; ++i) {
        var key = objKeys[i];

        if (skipNulls && obj[key] === null) {
            continue;
        }

        keys = keys.concat(_stringify(
            obj[key],
            key,
            generateArrayPrefix,
            strictNullHandling,
            skipNulls,
            encode ? encoder : null,
            filter,
            sort,
            allowDots,
            serializeDate,
            formatter,
            encodeValuesOnly,
            charset
        ));
    }

    var joined = keys.join(delimiter);
    var prefix = options.addQueryPrefix === true ? '?' : '';

    if (options.charsetSentinel) {
        if (charset === 'iso-8859-1') {
            // encodeURIComponent('&#10003;'), the "numeric entity" representation of a checkmark
            prefix += 'utf8=%26%2310003%3B&';
        } else {
            // encodeURIComponent('✓')
            prefix += 'utf8=%E2%9C%93&';
        }
    }

    return joined.length > 0 ? prefix + joined : '';
};



var qs = {
    stringify: stringify
};

var BASE_URL = "https://app-api.pixiv.net";
var CLIENT_ID = "KzEZED7aC0vird8jWyHM38mXjNTY";
var CLIENT_SECRET = "W9JZoJe00qPvJsiyCGT3CCtC6ZUtdpKpzMbNlUGP";
var filter = "for_ios";


function callApi(url, options) {
    var finalUrl = /^https?:\/\//i.test(url) ? url : BASE_URL + url;
    options.url = finalUrl;
    return axios.request(options)
        .then(function(res) {
            return res.data;
        })
        .catch(function(err) {
            if (err.response) {
                throw err.response.data;
            } else {
                throw err.message;
            }
        });
}


function PixivAPI() {
    this.headers = {
        "App-OS": "ios",
        "Accept-Language": "en-us",
        "App-OS-Version": "9.3.3",
        "App-Version": "7.1.11",
        "User-Agent": "PixivIOSApp/7.1.11 (iOS 9.0; iPhone8,2)"
    };
}

PixivAPI.prototype.login = function login(username, password, rememberPassword) {
    var _this = this;

    if (!username) {
        return Promise.reject(new Error("username required"));
    }
    if (!password) {
        return Promise.reject(new Error("password required"));
    }
    var data = qs.stringify({
        client_id: CLIENT_ID,
        client_secret: CLIENT_SECRET,
        get_secure_url: 1,
        grant_type: "password",
        username: username,
        password: password
    });
    var options = {
        method: "POST",
        adapter: xhrAdapter,
        url: "https://oauth.secure.pixiv.net/auth/token",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        data: data
    };
    TAJS_dumpValue('executed');
    return axios.request(options)
        .then(function(res) {
            _this.auth = res.data.response;
            // eslint-disable-next-line no-unneeded-ternary
            _this.rememberPassword = rememberPassword === false ? false : true;
            if (rememberPassword) {
                _this.username = username;
                _this.password = password;
            }
            return res.data.response;
        })
        .catch(function(err) {
            if (err.response) {
                throw err.response.data;
            } else {
                throw err.message;
            }
        });
};

PixivAPI.prototype.authInfo = function authInfo() {
    return this.auth;
};

PixivAPI.prototype.searchIllust = function searchIllust(word, options) {
    if (!word) {
        return Promise.reject(new Error("word required"));
    }

    var queryString = qs.stringify(
        Object.assign(
            {
                word: word,
                search_target: "partial_match_for_tags",
                sort: "date_desc",
                filter: filter
            },
            options
        )
    );
    return this.requestUrl("/v1/search/illust?" + queryString);
};

PixivAPI.prototype.requestUrl = function requestUrl(url, options) {
    TAJS_makeContextSensitive(this.requestUrl, 0);
    TAJS_makeContextSensitive(this.requestUrl, 1);
    var _this3 = this;

    if (!url) {
        return Promise.reject("Url cannot be empty");
    }
    options = options || {};
    options.headers = Object.assign(
        {},
        this.headers,
        options.headers || {}
    );
    if (this.auth && this.auth.access_token) {
        options.headers.Authorization = "Bearer " + this.auth.access_token;
    }
    return callApi(url, options)
        .then(function(json) {
            return json;
        })
        .catch(function(err) {
            if (_this3.rememberPassword) {
                if (_this3.username && _this3.password) {
                    return _this3
                        .login(_this3.username, _this3.password)
                        .then(function() {
                            options.headers.Authorization =
                                "Bearer " + _this3.auth.access_token;
                            return callApi(url, options);
                        });
                }
            }
            throw err;
        });
};


var PixivApi = (function() {
    function PixivApi() {
        _classCallCheck(this, PixivApi);

        this.headers = {
            "App-OS": "ios",
            "Accept-Language": "en-us",
            "App-OS-Version": "9.3.3",
            "App-Version": "7.1.11",
            "User-Agent": "PixivIOSApp/7.1.11 (iOS 9.0; iPhone8,2)"
        };
    }

    _createClass(PixivApi, [
        {
            key: "login",
            value: function login(username, password, rememberPassword) {
                var _this = this;

                if (!username) {
                    return Promise.reject(new Error("username required"));
                }
                if (!password) {
                    return Promise.reject(new Error("password required"));
                }
                var data = qs.stringify({
                    client_id: CLIENT_ID,
                    client_secret: CLIENT_SECRET,
                    get_secure_url: 1,
                    grant_type: "password",
                    username: username,
                    password: password
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                TAJS_dumpValue('executed');
                return axios("https://oauth.secure.pixiv.net/auth/token", options)
                    .then(function(res) {
                        _this.auth = res.data.response;
                        // eslint-disable-next-line no-unneeded-ternary
                        _this.rememberPassword = rememberPassword === false ? false : true;
                        if (rememberPassword) {
                            _this.username = username;
                            _this.password = password;
                        }
                        return res.data.response;
                    })
                    .catch(function(err) {
                        if (err.response) {
                            throw err.response.data;
                        } else {
                            throw err.message;
                        }
                    });
            }
        },
        {
            key: "logout",
            value: function logout() {
                this.auth = null;
                this.username = null;
                this.password = null;
                delete this.headers.Authorization;
                return Promise.resolve();
            }
        },
        {
            key: "authInfo",
            value: function authInfo() {
                return this.auth;
            }
        },
        {
            key: "refreshAccessToken",
            value: function refreshAccessToken(refreshToken) {
                var _this2 = this;

                if ((!this.auth || !this.auth.refresh_token) && !refreshToken) {
                    return Promise.reject(new Error("refresh_token required"));
                }
                var data = qs.stringify({
                    client_id: CLIENT_ID,
                    client_secret: CLIENT_SECRET,
                    get_secure_url: 1,
                    grant_type: "refresh_token",
                    refresh_token: refreshToken || this.auth.refresh_token
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return axios("https://oauth.secure.pixiv.net/auth/token", options)
                    .then(function(res) {
                        _this2.auth = res.data.response;
                        return res.data.response;
                    })
                    .catch(function(err) {
                        if (err.response) {
                            throw err.response.data;
                        } else {
                            throw err.message;
                        }
                    });
            }

            // eslint-disable-next-line class-methods-use-this
        },
        {
            key: "createProvisionalAccount",
            value: function createProvisionalAccount(nickname) {
                if (!nickname) {
                    return Promise.reject(new Error("nickname required"));
                }
                var data = qs.stringify({
                    ref: "pixiv_ios_app_provisional_account",
                    user_name: nickname
                });

                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        Authorization: "Bearer WHDWCGnwWA2C8PRfQSdXJxjXp0G6ULRaRkkd6t5B6h8"
                    },
                    data: data
                };
                return axios(
                    "https://accounts.pixiv.net/api/provisional-accounts/create",
                    options
                )
                    .then(function(res) {
                        return res.data.body;
                    })
                    .catch(function(err) {
                        if (err.response) {
                            throw err.response.data;
                        } else {
                            throw err.message;
                        }
                    });
            }

            // require auth
        },
        {
            key: "userState",
            value: function userState() {
                return this.requestUrl("/v1/user/me/state");
            }
        },
        {
            key: "editUserAccount",
            value: function editUserAccount(fields) {
                if (!fields) {
                    return Promise.reject(new Error("fields required"));
                }

                var data = qs.stringify(
                    {
                        current_password: fields.currentPassword,
                        new_user_account: fields.pixivId, // changeable once per account
                        new_password: fields.newPassword, // required if current account is provisional
                        new_mail_address: fields.email
                    },
                    { skipNulls: true }
                );
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };

                return this.requestUrl(
                    "https://accounts.pixiv.net/api/account/edit",
                    options
                );
            }
        },
        {
            key: "sendAccountVerificationEmail",
            value: function sendAccountVerificationEmail() {
                var options = {
                    method: "POST"
                };
                return this.requestUrl("/v1/mail-authentication/send", options);
            }
        },
        {
            key: "searchIllust",
            value: function searchIllust(word, options) {
                if (!word) {
                    return Promise.reject(new Error("word required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            sort: "date_desc",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/search/illust?" + queryString);
            }
        },
        {
            key: "searchIllustPopularPreview",
            value: function searchIllustPopularPreview(word, options) {
                if (!word) {
                    return Promise.reject(new Error("word required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl(
                    "/v1/search/popular-preview/illust?" + queryString
                );
            }
        },
        {
            key: "searchNovel",
            value: function searchNovel(word, options) {
                if (!word) {
                    return Promise.reject(new Error("word required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            sort: "date_desc",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/search/novel?" + queryString);
            }
        },
        {
            key: "searchNovelPopularPreview",
            value: function searchNovelPopularPreview(word, options) {
                if (!word) {
                    return Promise.reject(new Error("word required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl(
                    "/v1/search/popular-preview/novel?" + queryString
                );
            }
        },
        {
            key: "searchIllustBookmarkRanges",
            value: function searchIllustBookmarkRanges(word, options) {
                if (!word) {
                    return Promise.reject("word required");
                }
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl(
                    "/v1/search/bookmark-ranges/illust?" + queryString
                );
            }
        },
        {
            key: "searchNovelBookmarkRanges",
            value: function searchNovelBookmarkRanges(word, options) {
                if (!word) {
                    return Promise.reject("word required");
                }
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            word: word,
                            search_target: "partial_match_for_tags",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl(
                    "/v1/search/bookmark-ranges/novel?" + queryString
                );
            }
        },
        {
            key: "searchUser",
            value: function searchUser(word) {
                if (!word) {
                    return Promise.reject(new Error("word required"));
                }
                var queryString = qs.stringify(
                    Object.assign({
                        word: word,
                        filter: filter
                    })
                );
                return this.requestUrl("/v1/search/user?" + queryString);
            }
        },
        {
            key: "searchAutoComplete",
            value: function searchAutoComplete(word) {
                if (!word) {
                    return Promise.reject("word required");
                }
                var queryString = qs.stringify(
                    Object.assign({
                        word: word
                    })
                );
                return this.requestUrl("/v1/search/autocomplete?" + queryString);
            }
        },
        {
            key: "userDetail",
            value: function userDetail(id, options) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/detail?" + queryString);
            }
        },
        {
            key: "userIllusts",
            value: function userIllusts(id, options) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/illusts?" + queryString);
            }
        },
        {
            key: "userNovels",
            value: function userNovels(id, options) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/novels?" + queryString);
            }
        },
        {
            key: "userBookmarksIllust",
            value: function userBookmarksIllust(id, options) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            restrict: "public",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/bookmarks/illust?" + queryString);
            }
        },
        {
            key: "userBookmarkIllustTags",
            value: function userBookmarkIllustTags(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            restrict: "public"
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/bookmark-tags/illust?" + queryString);
            }
        },
        {
            key: "illustBookmarkDetail",
            value: function illustBookmarkDetail(id, options) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            illust_id: id
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/illust/bookmark/detail?" + queryString);
            }
        },
        {
            key: "userBookmarksNovel",
            value: function userBookmarksNovel(id, options) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            restrict: "public",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/bookmarks/novel?" + queryString);
            }
        },
        {
            key: "userBookmarkNovelTags",
            value: function userBookmarkNovelTags(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            restrict: "public"
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/bookmark-tags/novel?" + queryString);
            }
        },
        {
            key: "illustWalkthrough",
            value: function illustWalkthrough() {
                return this.requestUrl("/v1/walkthrough/illusts");
            }
        },
        {
            key: "illustComments",
            value: function illustComments(id, options) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            illust_id: id,
                            include_total_comments: true
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/comments?" + queryString);
            }
        },
        {
            key: "illustCommentsV2",
            value: function illustCommentsV2(id, options) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            illust_id: id
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/illust/comments?" + queryString);
            }
        },
        {
            key: "illustCommentReplies",
            value: function illustCommentReplies(id) {
                if (!id) {
                    return Promise.reject(new Error("comment_id required"));
                }
                var queryString = qs.stringify({ comment_id: id });
                return this.requestUrl("/v1/illust/comment/replies?" + queryString);
            }
        },
        {
            key: "illustRelated",
            value: function illustRelated(id, options) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            illust_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/illust/related?" + queryString);
            }
        },
        {
            key: "illustDetail",
            value: function illustDetail(id, options) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            illust_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/detail?" + queryString);
            }
        },
        {
            key: "illustNew",
            value: function illustNew(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            content_type: "illust",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/new?" + queryString);
            }
        },
        {
            key: "illustFollow",
            value: function illustFollow(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            restrict: "all"
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/illust/follow?" + queryString);
            }
        },
        {
            key: "illustRecommended",
            value: function illustRecommended(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            include_ranking_illusts: true,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/recommended?" + queryString);
            }
        },
        {
            key: "illustRanking",
            value: function illustRanking(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            mode: "day",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/ranking?" + queryString);
            }
        },
        {
            key: "illustMyPixiv",
            value: function illustMyPixiv() {
                return this.requestUrl("/v2/illust/mypixiv");
            }
        },
        {
            key: "illustAddComment",
            value: function illustAddComment(id, comment, parentCommentId) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }
                if (!comment) {
                    return Promise.reject(new Error("comment required"));
                }
                var data = qs.stringify({
                    illust_id: id,
                    comment: comment,
                    parent_comment_id: parentCommentId
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/illust/comment/add", options);
            }
        },
        {
            key: "novelAddComment",
            value: function novelAddComment(id, comment, parentCommentId) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }
                if (!comment) {
                    return Promise.reject(new Error("comment required"));
                }
                var data = qs.stringify({
                    novel_id: id,
                    comment: comment,
                    parent_comment_id: parentCommentId
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/novel/comment/add", options);
            }
        },
        {
            key: "trendingTagsIllust",
            value: function trendingTagsIllust(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/trending-tags/illust?" + queryString);
            }
        },
        {
            key: "trendingTagsNovel",
            value: function trendingTagsNovel(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/trending-tags/novel?" + queryString);
            }
        },
        {
            key: "bookmarkIllust",
            value: function bookmarkIllust(id, restrict, tags) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }
                if (restrict && ["public", "private"].indexOf(restrict) === -1) {
                    return Promise.reject(new Error("invalid restrict value"));
                }
                if (tags && !Array.isArray(tags)) {
                    return Promise.reject(new Error("invalid tags value"));
                }
                var data = qs.stringify({
                    illust_id: id,
                    restrict: restrict || "public",
                    tags: tags && tags.length ? tags : undefined
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v2/illust/bookmark/add", options);
            }
        },
        {
            key: "unbookmarkIllust",
            value: function unbookmarkIllust(id) {
                if (!id) {
                    return Promise.reject(new Error("illust_id required"));
                }
                var data = qs.stringify({
                    illust_id: id
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/illust/bookmark/delete", options);
            }
        },
        {
            key: "bookmarkNovel",
            value: function bookmarkNovel(id, restrict, tags) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }
                if (restrict && ["public", "private"].indexOf(restrict) === -1) {
                    return Promise.reject(new Error("invalid restrict value"));
                }
                if (tags && !Array.isArray(tags)) {
                    return Promise.reject(new Error("invalid tags value"));
                }
                var data = qs.stringify({
                    novel_id: id,
                    restrict: restrict || "public",
                    tags: tags && tags.length ? tags : undefined
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v2/novel/bookmark/add", options);
            }
        },
        {
            key: "unbookmarkNovel",
            value: function unbookmarkNovel(id) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }
                var data = qs.stringify({
                    novel_id: id
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/novel/bookmark/delete", options);
            }
        },
        {
            key: "followUser",
            value: function followUser(id, restrict) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }
                if (restrict && ["public", "private"].indexOf(restrict) === -1) {
                    return Promise.reject(new Error("invalid restrict value"));
                }
                var data = qs.stringify({
                    user_id: id,
                    restrict: restrict || "public"
                });
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/user/follow/add", options);
            }
        },
        {
            key: "unfollowUser",
            value: function unfollowUser(id) {
                if (!id) {
                    return Promise.reject(new Error("user_id required"));
                }
                var data = qs.stringify({
                    user_id: id,
                    restrict: "public"
                });
                //
                var options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    data: data
                };
                return this.requestUrl("/v1/user/follow/delete", options);
            }
        },
        {
            key: "mangaRecommended",
            value: function mangaRecommended(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            include_ranking_label: true,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/manga/recommended?" + queryString);
            }
        },
        {
            key: "mangaNew",
            value: function mangaNew(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            content_type: "manga",
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/illust/new?" + queryString);
            }
        },
        {
            key: "novelRecommended",
            value: function novelRecommended(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            include_ranking_novels: true,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/novel/recommended?" + queryString);
            }
        },
        {
            key: "novelNew",
            value: function novelNew(options) {
                var queryString = qs.stringify(options);
                return this.requestUrl("/v1/novel/new?" + queryString);
            }
        },
        {
            key: "novelComments",
            value: function novelComments(id, options) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            novel_id: id,
                            include_total_comments: true
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/novel/comments?" + queryString);
            }
        },
        {
            key: "novelCommentsV2",
            value: function novelCommentsV2(id, options) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            novel_id: id
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/novel/comments?" + queryString);
            }
        },
        {
            key: "novelCommentReplies",
            value: function novelCommentReplies(id) {
                if (!id) {
                    return Promise.reject(new Error("comment_id required"));
                }
                var queryString = qs.stringify({ comment_id: id });
                return this.requestUrl("/v1/novel/comment/replies?" + queryString);
            }
        },
        {
            key: "novelSeries",
            value: function novelSeries(id) {
                if (!id) {
                    return Promise.reject(new Error("series_id required"));
                }

                var queryString = qs.stringify({ series_id: id });
                return this.requestUrl("/v1/novel/series?" + queryString);
            }
        },
        {
            key: "novelDetail",
            value: function novelDetail(id) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }

                var queryString = qs.stringify({ novel_id: id });
                return this.requestUrl("/v2/novel/detail?" + queryString);
            }
        },
        {
            key: "novelText",
            value: function novelText(id) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }

                var queryString = qs.stringify({ novel_id: id });
                return this.requestUrl("/v1/novel/text?" + queryString);
            }
        },
        {
            key: "novelFollow",
            value: function novelFollow(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            restrict: "all"
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/novel/follow?" + queryString);
            }
        },
        {
            key: "novelMyPixiv",
            value: function novelMyPixiv() {
                return this.requestUrl("/v1/novel/mypixiv");
            }
        },
        {
            key: "novelRanking",
            value: function novelRanking(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            mode: "day"
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/novel/ranking?" + queryString);
            }
        },
        {
            key: "novelBookmarkDetail",
            value: function novelBookmarkDetail(id, options) {
                if (!id) {
                    return Promise.reject(new Error("novel_id required"));
                }

                var queryString = qs.stringify(
                    Object.assign(
                        {
                            novel_id: id
                        },
                        options
                    )
                );
                return this.requestUrl("/v2/novel/bookmark/detail?" + queryString);
            }
        },
        {
            key: "userRecommended",
            value: function userRecommended(options) {
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/recommended?" + queryString);
            }
        },
        {
            key: "userFollowing",
            value: function userFollowing(id, options) {
                if (!id) {
                    return Promise.reject("user_id required");
                }
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            restrict: "public"
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/following?" + queryString);
            }
        },
        {
            key: "userFollowDetail",
            value: function userFollowDetail(id) {
                if (!id) {
                    return Promise.reject("user_id required");
                }
                var queryString = qs.stringify({ user_id: id });
                return this.requestUrl("/v1/user/follow/detail?" + queryString);
            }
        },
        {
            key: "userFollower",
            value: function userFollower(id, options) {
                if (!id) {
                    return Promise.reject("user_id required");
                }
                var queryString = qs.stringify(
                    Object.assign(
                        {
                            user_id: id,
                            filter: filter
                        },
                        options
                    )
                );
                return this.requestUrl("/v1/user/follower?" + queryString);
            }
        },
        {
            key: "userMyPixiv",
            value: function userMyPixiv(id) {
                if (!id) {
                    return Promise.reject("user_id required");
                }
                var queryString = qs.stringify({ user_id: id });
                return this.requestUrl("/v1/user/mypixiv?" + queryString);
            }
        },
        {
            key: "ugoiraMetaData",
            value: function ugoiraMetaData(id) {
                if (!id) {
                    return Promise.reject("illust_id required");
                }
                var queryString = qs.stringify({ illust_id: id });
                return this.requestUrl("/v1/ugoira/metadata?" + queryString);
            }
        },
        {
            key: "setLanguage",
            value: function setLanguage(lang) {
                this.headers["Accept-Language"] = lang;
            }
        },
        {
            key: "requestUrl",
            value: function requestUrl(url, options) {
                var _this3 = this;

                if (!url) {
                    return Promise.reject("Url cannot be empty");
                }
                options = options || {};
                options.headers = Object.assign(
                    {},
                    this.headers,
                    options.headers || {}
                );
                if (this.auth && this.auth.access_token) {
                    options.headers.Authorization = "Bearer " + this.auth.access_token;
                }
                return callApi(url, options)
                    .then(function(json) {
                        return json;
                    })
                    .catch(function(err) {
                        if (_this3.rememberPassword) {
                            if (_this3.username && _this3.password) {
                                return _this3
                                    .login(_this3.username, _this3.password)
                                    .then(function() {
                                        options.headers.Authorization =
                                            "Bearer " + _this3.auth.access_token;
                                        return callApi(url, options);
                                    });
                            }
                        }
                        throw err;
                    });
            }
        }
    ]);

    return PixivApi;
})();


function test() {
    var pixiv = new PixivAPI();
    pixiv.login('username', 'password').then(function ff1() {
        TAJS_dumpValue('ff last');
        return pixiv.searchIllust('ff', {});
    }).then(function ff2() {
    });
    pixiv.authInfo();
}