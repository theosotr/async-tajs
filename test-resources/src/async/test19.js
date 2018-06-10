var x = Promise.resolve('foo');

var y = x
  .then(JSON.parse)
  .then(function ff1() {
      TAJS_dumpValue('executed');
  });


function foo() {
    return 'foo'
}

function bar(param) {
    return param + foo();
}

var z = Promise.resolve('bar');
z
  .then(bar)
  .then(function ff2(value) {
      TAJS_assertEquals(value, 'barfoo');
  });


function baz(param) {
    if (param)
        return 'foo';
    else
        return 'bar';
}

baz(true);
var p = Promise.resolve(false);
p.then(baz).then(function ff3(value) {
    TAJS_dumpValue(value);
});
