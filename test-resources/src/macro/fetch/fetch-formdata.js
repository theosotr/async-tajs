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
    return ""
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
    reader.onload = function() {
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
        return this.text().then(parse);
    };

    return this;
}

// HTTP methods whose capitalization should be normalized
var methods = ["DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"];

function normalizeMethod(method) {
    var upcased = method.toUpperCase();
    return methods.indexOf(upcased) > -1 ? upcased : method;
}

function Request(input, options) {
    options = options || {};
    var body = options.body;

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

function parse(value) {
    return JSON.parse(value);
}

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
    preProcessedHeaders.split(/\r?\n/).forEach(function(line) {
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
            return reject(new DOMException("Aborted", "AbortError"));
        }

        var xhr = new XMLHttpRequest();

        function abortXhr() {
            xhr.abort();
        }

        xhr.onload = function() {
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
            reject(new TypeError("Network request failed"));
        };

        xhr.ontimeout = function() {
            reject(new TypeError("Network request failed"));
        };

        xhr.onabort = function() {
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

fetch.polyfill = true;

if (!self.fetch) {
    self.fetch = fetch;
    self.Headers = Headers;
    self.Request = Request;
    self.Response = Response;
}

function test1() {
    var r = new Response('fda');
    r.formData();

    var r2 = new Response(new Blob());
    r2.formData().then(function ff1(value) {

    });
}

test1();
