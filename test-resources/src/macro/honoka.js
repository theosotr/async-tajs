// DEPENDENCIES AND STUBS
'use strict';
// STUBS
// XMLHttpRequest
function XMLHttpRequest() { }
XMLHttpRequest.prototype.onload = function defaultXMLOnload() { };
XMLHttpRequest.prototype.onerror = function defaultOnError() { };
XMLHttpRequest.prototype.send = function () {
    this.responseURL = TAJS_make('AnyStr');
    this.responseText = TAJS_make('AnyStr');
    TAJS_addAsyncIOCallback(this.onload);
};
XMLHttpRequest.prototype.open = function (method) { };
XMLHttpRequest.prototype.abort = function () {  };
XMLHttpRequest.prototype.getAllResponseHeaders = function () {
    return 'Content-Type: text/html; charset=utf-8\\r\\n'
};
XMLHttpRequest.prototype.setRequestHeader = function (header) { };


// FileReader
function FileReader() { }
FileReader.prototype.onload = function defaultOnload() { };
FileReader.prototype.onerror = function defaultOnError() { };
FileReader.prototype.readAsArrayBuffer = function (blob) {
    this.result = TAJS_make('AnyStr');
    TAJS_addAsyncIOCallback(this.onload);
};
FileReader.prototype.readAsText = function (blob) {
    this.result = TAJS_make('AnyStr');
    TAJS_addAsyncIOCallback(this.onload);
};

// Blob
function Blob() { }
function FormData() { }
FormData.prototype.append = function (name) { };
function ArrayBuffer() { }
function DataView() {

}
var exports = {};
self = this;




// PROGRAM BEGINS
Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.Headers = Headers;
exports.Request = Request;
exports.Response = Response;
exports.fetch = fetch;
var support = {
    searchParams: "URLSearchParams" in self,
    iterable: "Symbol" in self && "iterator" in Symbol,
    blob:
    "FileReader" in self &&
    "Blob" in self &&
    (function() {
        try {
            new Blob();
            return true;
        } catch (e) {
            return false;
        }
    })(),
    formData: "FormData" in self,
    arrayBuffer: "ArrayBuffer" in self
};

function isDataView(obj) {
    return obj && DataView.prototype.isPrototypeOf(obj);
}

if (support.arrayBuffer) {
    var viewClasses = [
        "[object Int8Array]",
        "[object Uint8Array]",
        "[object Uint8ClampedArray]",
        "[object Int16Array]",
        "[object Uint16Array]",
        "[object Int32Array]",
        "[object Uint32Array]",
        "[object Float32Array]",
        "[object Float64Array]"
    ];

    var isArrayBufferView =
        ArrayBuffer.isView ||
        function(obj) {
            return (
                obj && viewClasses.indexOf(Object.prototype.toString.call(obj)) > -1
            );
        };
}

function normalizeName(name) {
    TAJS_makeContextSensitive(normalizeName, 0);
    if (typeof name !== "string") {
        name = String(name);
    }
    if (/[^a-z0-9\-#$%&'*+.^_`|~]/i.test(name)) {
        throw new TypeError("Invalid character in header field name");
    }
    return name.toLowerCase();
}

function normalizeValue(value) {
    TAJS_makeContextSensitive(normalizeValue, 0);
    if (typeof value !== "string") {
        value = String(value);
    }
    return value;
}

// Build a destructive iterator for the value list
function iteratorFor(items) {
    var iterator = {
        next: function next() {
            var value = items.shift();
            return { done: value === undefined, value: value };
        }
    };

    if (support.iterable) {
        iterator[Symbol.iterator] = function() {
            return iterator;
        };
    }

    return iterator;
}

function Headers(headers) {
    TAJS_makeContextSensitive(Headers, 0);
    this.map = {};

    if (headers instanceof Headers) {
        headers.forEach(function(value, name) {
            this.append(name, value);
        }, this);
    } else if (Array.isArray(headers)) {
        headers.forEach(function(header) {
            this.append(header[0], header[1]);
        }, this);
    } else if (headers) {
        Object.getOwnPropertyNames(headers).forEach(function(name) {
            this.append(name, headers[name]);
        }, this);
    }
}

Headers.prototype.append = function(name, value) {
    TAJS_makeContextSensitive(this.append, 0);
    name = normalizeName(name);
    value = normalizeValue(value);
    var oldValue = this.map[name];
    this.map[name] = oldValue ? oldValue + ", " + value : value;
};

Headers.prototype["delete"] = function(name) {
    delete this.map[normalizeName(name)];
};

Headers.prototype.get = function(name) {
    TAJS_makeContextSensitive(this.get, 0);
    name = normalizeName(name);
    return this.has(name) ? this.map[name] : null;
};

Headers.prototype.has = function(name) {
    TAJS_makeContextSensitive(this.has, 0);
    return this.map.hasOwnProperty(normalizeName(name));
};

Headers.prototype.set = function(name, value) {
    TAJS_makeContextSensitive(this.set, 0);
    this.map[normalizeName(name)] = normalizeValue(value);
};

Headers.prototype.forEach = function(callback, thisArg) {
    TAJS_makeContextSensitive(this.forEach, -1);
    TAJS_makeContextSensitive(this.forEach, 0);
    TAJS_makeContextSensitive(this.forEach, 1);
    for (var name in this.map) {
        if (this.map.hasOwnProperty(name)) {
            callback.call(thisArg, this.map[name], name, this);
        }
    }
};

Headers.prototype.keys = function() {
    var items = [];
    this.forEach(function(value, name) {
        items.push(name);
    });
    return iteratorFor(items);
};

Headers.prototype.values = function() {
    var items = [];
    this.forEach(function(value) {
        items.push(value);
    });
    return iteratorFor(items);
};

Headers.prototype.entries = function() {
    var items = [];
    this.forEach(function(value, name) {
        items.push([name, value]);
    });
    return iteratorFor(items);
};

if (support.iterable) {
    Headers.prototype[Symbol.iterator] = Headers.prototype.entries;
}

function consumed(body) {
    TAJS_makeContextSensitive(consumed, 0);
    TAJS_dumpValue(body);
    TAJS_dumpValue(body.bodyUsed);
    if (body.bodyUsed) {
        TAJS_dumpValue('reach here');
        return Promise.reject(new TypeError("Already read"));
    }
    body.bodyUsed = true;
}


function fileReaderReady(reader) {
    TAJS_makeContextSensitive(fileReaderReady, 0);
    var res;
    var rej;
    var promise = new Promise(function exec(resolve, reject) {
        TAJS_makeContextSensitive(exec, 0);
        TAJS_makeContextSensitive(exec, 1);
        res = resolve;
        rej = reject;
    });
    reader.onload = function onLoad() {
        res(reader.result);
    };
    reader.onerror = function() {
        rej(reader.error);
    };
    return promise;
}

function readBlobAsArrayBuffer(blob) {
    var reader = new FileReader();
    var promise = fileReaderReady(reader);
    reader.readAsArrayBuffer(blob);
    return promise;
}

function readBlobAsText(blob) {
    var reader = new FileReader();
    var promise = fileReaderReady(reader);
    reader.readAsText(blob);
    return promise;
}

function readArrayBufferAsText(buf) {
    var view = new Uint8Array(buf);
    var chars = new Array(view.length);

    for (var i = 0; i < view.length; i++) {
        chars[i] = String.fromCharCode(view[i]);
    }
    return chars.join("");
}

function bufferClone(buf) {
    TAJS_makeContextSensitive(bufferClone, 0);
    if (buf.slice) {
        return buf.slice(0);
    } else {
        var view = new Uint8Array(buf.byteLength);
        view.set(new Uint8Array(buf));
        return view.buffer;
    }
}

function Body() {
    this.bodyUsed = false;

    this._initBody = function initBody(body) {
        TAJS_makeContextSensitive(initBody, -2);
        TAJS_makeContextSensitive(initBody, -1);
        TAJS_makeContextSensitive(initBody, 0);
        this._bodyInit = body;
        TAJS_dumpValue(body);
        if (!body) {
            this._bodyText = "";
        } else if (typeof body === "string") {
            this._bodyText = body;
        } else if (support.blob && Blob.prototype.isPrototypeOf(body)) {
            this._bodyBlob = body;
        } else if (support.formData && FormData.prototype.isPrototypeOf(body)) {
            this._bodyFormData = body;
        } else if (
            support.searchParams &&
            URLSearchParams.prototype.isPrototypeOf(body)
        ) {
            this._bodyText = body.toString();
        } else if (support.arrayBuffer && support.blob && isDataView(body)) {
            this._bodyArrayBuffer = bufferClone(body.buffer);
            // IE 10-11 can't handle a DataView body.
            this._bodyInit = new Blob([this._bodyArrayBuffer]);
        } else if (
            support.arrayBuffer &&
            (ArrayBuffer.prototype.isPrototypeOf(body) || isArrayBufferView(body))
        ) {
            this._bodyArrayBuffer = bufferClone(body);
        } else {
            throw new Error("unsupported BodyInit type");
        }

        if (!this.headers.get("content-type")) {
            if (typeof body === "string") {
                this.headers.set("content-type", "text/plain;charset=UTF-8");
            } else if (this._bodyBlob && this._bodyBlob.type) {
                this.headers.set("content-type", this._bodyBlob.type);
            } else if (
                support.searchParams &&
                URLSearchParams.prototype.isPrototypeOf(body)
            ) {
                this.headers.set(
                    "content-type",
                    "application/x-www-form-urlencoded;charset=UTF-8"
                );
            }
        }
    };

    if (support.blob) {
        this.blob = function() {
            var rejected = consumed(this);
            if (rejected) {
                return rejected;
            }

            if (this._bodyBlob) {
                return Promise.resolve(this._bodyBlob);
            } else if (this._bodyArrayBuffer) {
                return Promise.resolve(new Blob([this._bodyArrayBuffer]));
            } else if (this._bodyFormData) {
                throw new Error("could not read FormData body as blob");
            } else {
                return Promise.resolve(new Blob([this._bodyText]));
            }
        };

        this.arrayBuffer = function() {
            if (this._bodyArrayBuffer) {
                return consumed(this) || Promise.resolve(this._bodyArrayBuffer);
            } else {
                return this.blob().then(readBlobAsArrayBuffer);
            }
        };
    }

    this.text = function text() {
        TAJS_makeContextSensitive(text, -1);
        TAJS_makeContextSensitive(text, -2);
        var rejected = consumed(this);
        if (rejected) {
            TAJS_dumpValue(rejected);
            return rejected;
        }

        if (this._bodyBlob) {
            return readBlobAsText(this._bodyBlob);
        } else if (this._bodyArrayBuffer) {
            return Promise.resolve(readArrayBufferAsText(this._bodyArrayBuffer));
        } else if (this._bodyFormData) {
            throw new Error("could not read FormData body as text");
        } else {
            return Promise.resolve(this._bodyText);
        }
    };

    if (support.formData) {
        this.formData = function() {
            TAJS_makeContextSensitive(this.formData, -1);
            return this.text().then(decode);
        };
    }

    this.json = function json() {
        TAJS_makeContextSensitive(json, -1);
        var val = this.text();
        TAJS_dumpValue(val);
        return val.then(function (value) {
            return JSON.parse(value);
        });
    };

    return this;
}

// HTTP methods whose capitalization should be normalized
var methods = ["DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"];

function normalizeMethod(method) {
    TAJS_makeContextSensitive(normalizeMethod, 0);
    var upcased = method.toUpperCase();
    return methods.indexOf(upcased) > -1 ? upcased : method;
}

function Request(input, options) {
    options = options || {};
    var body = options.body;
    TAJS_dumpValue(options.method);

    if (input instanceof Request) {
        if (input.bodyUsed) {
            throw new TypeError("Already read");
        }
        this.url = input.url;
        this.credentials = input.credentials;
        if (!options.headers) {
            this.headers = new Headers(input.headers);
        }
        this.method = input.method;
        this.mode = input.mode;
        this.signal = input.signal;
        if (!body && input._bodyInit != null) {
            body = input._bodyInit;
            input.bodyUsed = true;
        }
    } else {
        this.url = String(input);
    }

    this.credentials = options.credentials || this.credentials || "same-origin";
    if (options.headers || !this.headers) {
        this.headers = new Headers(options.headers);
    }
    this.method = normalizeMethod(options.method || this.method || "GET");
    this.mode = options.mode || this.mode || null;
    this.signal = options.signal || this.signal;
    this.referrer = null;

    if ((this.method === "GET" || this.method === "HEAD") && body) {
        throw new TypeError("Body not allowed for GET or HEAD requests");
    }
    this._initBody(body);
}

Request.prototype.clone = function() {
    return new Request(this, { body: this._bodyInit });
};

function decode(body) {
    TAJS_makeContextSensitive(decode, 0);
    var form = new FormData();
    body
        .trim()
        .split("&")
        .forEach(function ff(bytes) {
            TAJS_makeContextSensitive(ff, 0);
            if (bytes) {
                var split = bytes.split("=");
                var name = split.shift().replace(/\+/g, " ");
                var value = split.join("=").replace(/\+/g, " ");
                form.append(decodeURIComponent(name), decodeURIComponent(value));
            }
        });
    return form;
}

function parseHeaders(rawHeaders) {
    TAJS_makeContextSensitive(parseHeaders, 0);
    var headers = new Headers();
    // Replace instances of \r\n and \n followed by at least one space or horizontal tab with a space
    // https://tools.ietf.org/html/rfc7230#section-3.2
    var preProcessedHeaders = rawHeaders.replace(/\r?\n[\t ]+/g, " ");
    preProcessedHeaders.split(/\r?\n/).forEach(function forf(line) {
        TAJS_makeContextSensitive(forf, 0);
        var parts = line.split(":");
        var key = parts.shift().trim();
        if (key) {
            var value = parts.join(":").trim();
            headers.append(key, value);
        }
    });
    return headers;
}

Body.call(Request.prototype);

function Response(bodyInit, options) {
    TAJS_makeContextSensitive(Response, 0);
    TAJS_makeContextSensitive(Response, 1);
    if (!options) {
        options = {};
    }

    this.type = "default";
    this.status = options.status === undefined ? 200 : options.status;
    this.ok = this.status >= 200 && this.status < 300;
    this.statusText = "statusText" in options ? options.statusText : "OK";
    this.headers = new Headers(options.headers);
    this.url = options.url || "";
    this._initBody(bodyInit);
}

Body.call(Response.prototype);

Response.prototype.clone = function() {
    return new Response(this._bodyInit, {
        status: this.status,
        statusText: this.statusText,
        headers: new Headers(this.headers),
        url: this.url
    });
};

Response.error = function() {
    var response = new Response(null, { status: 0, statusText: "" });
    response.type = "error";
    return response;
};

var redirectStatuses = [301, 302, 303, 307, 308];

Response.redirect = function(url, status) {
    if (redirectStatuses.indexOf(status) === -1) {
        throw new RangeError("Invalid status code");
    }

    return new Response(null, { status: status, headers: { location: url } });
};

var DOMException = (exports.DOMException = self.DOMException);
try {
    new DOMException();
} catch (err) {
    exports.DOMException = DOMException = function DOMException(message, name) {
        this.message = message;
        this.name = name;
        var error = Error(message);
        this.stack = error.stack;
    };
    DOMException.prototype = Object.create(Error.prototype);
    DOMException.prototype.constructor = DOMException;
}

function fetch(input, init) {
    return new Promise(function F(resolve, reject) {
        var request = new Request(input, init);

        if (request.signal && request.signal.aborted) {
            TAJS_dumpValue('Im here');
            return reject(new DOMException("Aborted", "AbortError"));
        }

        var xhr = new XMLHttpRequest();

        function abortXhr() {
            xhr.abort();
        }

        xhr.onload = function request() {
            var options = {
                status: xhr.status,
                statusText: xhr.statusText,
                headers: parseHeaders(xhr.getAllResponseHeaders() || "")
            };
            options.url =
                "responseURL" in xhr
                    ? xhr.responseURL
                    : options.headers.get("X-Request-URL");
            var body = "response" in xhr ? xhr.response : xhr.responseText;
            TAJS_dumpValue(body);
            resolve(new Response(body, options));
        };

        xhr.onerror = function onError() {
            TAJS_dumpValue('Im here');
            reject(new TypeError("Network request failed"));
        };

        xhr.ontimeout = function() {
            TAJS_dumpValue('Im here');
            reject(new TypeError("Network request failed"));
        };

        xhr.onabort = function() {
            TAJS_dumpValue('Im here');
            reject(new DOMException("Aborted", "AbortError"));
        };

        xhr.open(request.method, request.url, true);

        if (request.credentials === "include") {
            xhr.withCredentials = true;
        } else if (request.credentials === "omit") {
            xhr.withCredentials = false;
        }

        if ("responseType" in xhr && support.blob) {
            xhr.responseType = "blob";
        }

        request.headers.forEach(function(value, name) {
            xhr.setRequestHeader(name, value);
        });

        if (request.signal) {
            request.signal.addEventListener("abort", abortXhr);

            xhr.onreadystatechange = function() {
                // DONE (success or failure)
                if (xhr.readyState === 4) {
                    request.signal.removeEventListener("abort", abortXhr);
                }
            };
        }

        xhr.send(
            typeof request._bodyInit === "undefined" ? null : request._bodyInit
        );
    });
}




// PROGRAM
var defaults = {
    baseURL: "",
    timeout: 0,
    method: "get",
    headers: {
        delete: {},
        head: {},
        get: {},
        post: {
            'Content-Type': 'application/json'
        },
        put: {
            'Content-Type': 'application/json'
        },
        patch: {
            'Content-Type': 'application/json'
        }
    },
    dataType: "auto",
    expectedStatus: function expectedStatus(status) {
        return status >= 200 && status < 400;
    }
};


var methods2 = ['get', 'delete', 'head', 'options', 'post', 'put', 'patch'];


var spaceChars = ' \\s\u00A0';
var symbolRegex = /([[\]().?/*{}+$^:])/g;

function trimStart(str) {
    var charlist =
        arguments.length > 1 && arguments[1] !== undefined
            ? arguments[1]
            : spaceChars;

    charlist = (charlist + "").replace(symbolRegex, "$1");
    var re = new RegExp("^[" + charlist + "]+", "g");
    return String(str).replace(re, "");
}


function trimEnd(str) {
    var charlist =
        arguments.length > 1 && arguments[1] !== undefined
            ? arguments[1]
            : spaceChars;

    charlist = (charlist + "").replace(symbolRegex, "\\$1");
    var re = new RegExp("[" + charlist + "]+$", "g");
    return String(str).replace(re, "");
}


var toString = Object.prototype.toString;


function isObject(value) {
    return value !== null && typeof value === 'object';
}


function isArray(value) {
    return toString.call(value) === '[object Array]';
}


function isString(value) {
    return typeof value === 'string';
}


function isFormData(value) {
    return typeof FormData !== 'undefined' && value instanceof FormData;
}


function isNode() {
    if (
        typeof global.process !== 'undefined' &&
        /* istanbul ignore next */ global.process.versions &&
        /* istanbul ignore next */ global.process.versions.node
    ) {
        /* istanbul ignore next */ return true;
    }
    return false;
}


function isAbsoluteURL(url) {
    return /^(?:[a-z]+:)?\/\//i.test(url);
}


function buildURL(url, params) {
    if (!params) {
        return url;
    }

    var uris = [];

    function forEach1(collection, callback, scope) {
        if (Object.prototype.toString.call(collection) === "[object Object]") {
            for (var prop in collection) {
                if (Object.prototype.hasOwnProperty.call(collection, prop)) {
                    callback.call(scope, collection[prop], prop, collection);
                }
            }
        } else {
            for (var i = 0, len = collection.length; i < len; i++) {
                callback.call(scope, collection[i], i, collection);
            }
        }
    }

    forEach1(params, function(value, key) {
        if (isObject(value)) {
            value = JSON.stringify(value);
        }
        uris.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
    });

    url += (url.indexOf("?") === -1 ? "?" : "&") + uris.join("&");

    return url;
}

function forEach3(collection, callback, scope) {
    if (Object.prototype.toString.call(collection) === "[object Object]") {
        for (var prop in collection) {
            if (Object.prototype.hasOwnProperty.call(collection, prop)) {
                callback.call(scope, collection[prop], prop, collection);
            }
        }
    } else {
        for (var i = 0, len = collection.length; i < len; i++) {
            callback.call(scope, collection[i], i, collection);
        }
    }
}


function normalizeHeadersHonoka(headers) {
    TAJS_makeContextSensitive(normalizeHeadersHonoka, 0);
    var ucFirst = function ucFirst(str) {
        str = String(str);
        return str.charAt(0).toUpperCase() + str.substr(1);
    };

    forEach3(headers, function(value, key) {
        if (methods2.indexOf(key) === -1) {
            var normalizedKey = ucFirst(
                key
                    .toLowerCase()
                    .replace("_", "-")
                    .replace(/-(\w)/g, function($0, $1) {
                        return "-" + ucFirst($1);
                    })
            );
            delete headers[key];
            headers[normalizedKey] = value;
        }
    });
}


var interceptors = {};
var container = [];

interceptors.register = function(interceptor) {
    container.push(interceptor);
    return function() {
        var index = container.indexOf(interceptor);
        if (index >= 0) {
            container.splice(index, 1);
        }
    };
};

interceptors.clear = function() {
    container.length = 0;
};

interceptors.get = function() {
    return container;
};



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

function _toConsumableArray(arr) {
    if (Array.isArray(arr)) {
        for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) {
            arr2[i] = arr[i];
        }
        return arr2;
    } else {
        return Array.from(arr);
    }
}


var stringifyPrimitive = function stringifyPrimitive(v) {
    switch (typeof v === "undefined" ? "undefined" : _typeof(v)) {
        case "string":
            return v;

        case "boolean":
            return v ? "true" : "false";

        case "number":
            return isFinite(v) ? v : "";

        default:
            return "";
    }
};

var qsEncode = function(obj, sep, eq, name) {
    sep = sep || "&";
    eq = eq || "=";
    if (obj === null) {
        obj = undefined;
    }

    if ((typeof obj === "undefined" ? "undefined" : _typeof(obj)) === "object") {
        return Object.keys(obj)
            .map(function(k) {
                var ks = encodeURIComponent(stringifyPrimitive(k)) + eq;
                if (Array.isArray(obj[k])) {
                    return obj[k]
                        .map(function(v) {
                            return ks + encodeURIComponent(stringifyPrimitive(v));
                        })
                        .join(sep);
                } else {
                    return ks + encodeURIComponent(stringifyPrimitive(obj[k]));
                }
            })
            .filter(Boolean)
            .join(sep);
    }

    if (!name) return "";
    return (
        encodeURIComponent(stringifyPrimitive(name)) +
        eq +
        encodeURIComponent(stringifyPrimitive(obj))
    );
};


var forEach4 = function forEach(collection, callback, scope) {
    if (Object.prototype.toString.call(collection) === "[object Object]") {
        for (var prop in collection) {
            if (Object.prototype.hasOwnProperty.call(collection, prop)) {
                callback.call(scope, collection[prop], prop, collection);
            }
        }
    } else {
        for (var i = 0, len = collection.length; i < len; i++) {
            callback.call(scope, collection[i], i, collection);
        }
    }
};


function honoka(url, options) {
    options = options !== undefined ? options : {};

    //options = merge(defaults, options);
    if (options.headers === undefined)
        options.headers = {};

    options.method = options.method.toLowerCase();

    if (typeof url !== "string") {
        throw new TypeError(
            "Argument 1 expected string but got " +
            (typeof url === "undefined" ? "undefined" : _typeof(url))
        );
    }

    if (!isAbsoluteURL(url)) {
        url = trimEnd(options.baseURL, "/") + "/" + trimStart(url, "/");
    }

    if (options.method === "get" && isObject(options.data)) {
        url = buildURL(url, options.data);
    }

    normalizeHeadersHonoka(options.headers);

    // Set default headers for specified methods
    var methodDefaultHeaders = defaults.headers[options.method];
    if (isObject(methodDefaultHeaders)) {
        //options.headers = merge(methodDefaultHeaders, options.headers);
    }

    function forEach1(collection, callback, scope) {
        if (Object.prototype.toString.call(collection) === "[object Object]") {
            for (var prop in collection) {
                if (Object.prototype.hasOwnProperty.call(collection, prop)) {
                    callback.call(scope, collection[prop], prop, collection);
                }
            }
        } else {
            for (var i = 0, len = collection.length; i < len; i++) {
                callback.call(scope, collection[i], i, collection);
            }
        }
    }

    forEach1(methods2, function(method) {
        return delete options.headers[method];
    });

    var isContentTypeString = isString(options.headers["Content-Type"]);

    if (
        isContentTypeString &&
        options.headers["Content-Type"].match(/application\/json/i)
    ) {
        options.body = JSON.stringify(options.data);
    } else if (
        isContentTypeString &&
        options.headers["Content-Type"].match(/application\/x-www-form-urlencoded/i)
    ) {
        options.body = qsEncode(options.data);
    } else if (
        options.data &&
        options.method !== "get" &&
        options.method !== "head"
    ) {
        options.body = options.data;
    }

    if (
        isFormData(options.data) ||
        (isContentTypeString &&
            options.headers["Content-Type"].match(/multipart\/form-data/i))
    ) {
        delete options.headers["Content-Type"];
    }

    // parse interceptors
    var reversedInterceptors = interceptors.get();

    function forEach2(collection, callback, scope) {
        if (Object.prototype.toString.call(collection) === "[object Object]") {
            for (var prop in collection) {
                if (Object.prototype.hasOwnProperty.call(collection, prop)) {
                    callback.call(scope, collection[prop], prop, collection);
                }
            }
        } else {
            for (var i = 0, len = collection.length; i < len; i++) {
                callback.call(scope, collection[i], i, collection);
            }
        }
    }

    forEach2(reversedInterceptors, function(interceptor) {
        if (interceptor.request) {
            var interceptedOptions = interceptor.request(options);
            if (isObject(interceptedOptions)) {
                options = interceptedOptions;
            } else {
                throw new Error(
                    "Apply request interceptor failed, please check your interceptor"
                );
            }
        }
    });

    TAJS_dumpValue(options.method);
    return new Promise(function(resolve, reject) {
        if (options.timeout > 0) {
            setTimeout(function() {
                reject(new Error("Request timeout"));
            }, options.timeout);
        }

        fetch(url, options)
            .then(function onResponse(response) {
                honoka.response = response.clone();

                switch (options.dataType.toLowerCase()) {
                    case "arraybuffer":
                        return honoka.response.arrayBuffer();
                    case "blob":
                        return honoka.response.blob();
                    case "json":
                        return honoka.response.json();
                    case "buffer":
                        if (!isNode()) {
                            reject(new Error('"buffer" is not supported in browser'));
                        }
                        return honoka.response.buffer();
                    case "text":
                    case "":
                    case "auto":
                        return honoka.response.text();
                    default:
                        return honoka.response.text();
                }
            })
            .then(function onResponse2(responseData) {
                if (options.dataType === "" || options.dataType === "auto") {
                    var contentType = honoka.response.headers.get("Content-Type");
                    if (contentType && contentType.match(/application\/json/i)) {
                        responseData = JSON.parse(responseData);
                    }
                }

                forEach4(reversedInterceptors, function(interceptor) {
                    if (interceptor.response) {
                        var interceptedResponse = interceptor.response(
                            responseData,
                            honoka.response
                        );
                        if (
                            isArray(interceptedResponse) &&
                            interceptedResponse.length === 2
                        ) {
                            responseData = interceptedResponse[0];
                            honoka.response = interceptedResponse[1];
                        } else {
                            reject(
                                new Error(
                                    "Apply response interceptor failed, please check your interceptor"
                                )
                            );
                        }
                    }
                });

                if (options.expectedStatus(honoka.response.status)) {
                    resolve(responseData);
                } else {
                    reject(
                        new Error("Unexpected status code: " + honoka.response.status)
                    );
                }
            })
            .catch(reject);
    });
}


function test1() {
    honoka('/user/12345', {
        method: 'GET',
        dataType: 'json',
        expectedStatus: function expectedStatus(status) {
            return status >= 200 && status < 400;
        }
    }).then(function ff1() {
        TAJS_dumpValue('last');
    });
}

test1();