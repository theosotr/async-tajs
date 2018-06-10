function foo(param) {
    if (param)
        return 'foo';
    else
        return 'bar';
}

function bar(param) {
    // ensure that this call to `foo` does not resolve any promise.
    var x = foo(param);
    return x + ' appended text';
}

var x = Promise.resolve(false);
x
  .then(foo)
  .then(function baz(value) {
     TAJS_dumpValue(value);
     return true;
  })
  .then(bar)
  .then(function fn(value) {
      TAJS_dumpValue(value);
  });


function baz() {
    throw 'e'
}

function lala() {
    try {
        baz();
    } catch (e) {
        return 'foo'
    }
}

var y = Promise.resolve('');
y
  .then(lala)
  .then(function la(value){
      TAJS_assertEquals(value, 'foo');
  });
