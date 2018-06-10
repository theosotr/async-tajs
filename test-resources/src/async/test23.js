function foo(param) {
    return new Promise(function (resolve, reject) {
        if (param)
            resolve('bar');
        else
            reject('foo')
    });
}

// x should be resolved by 'bar'.
foo(true);
// Now, x should be both resolved by 'bar' and rejected by 'foo'.
var x = foo(false);
TAJS_dumpQueue();


function bar(param) {
    return Promise.resolve(param);
}
bar('foo');
bar('bar');
TAJS_dumpQueue();
