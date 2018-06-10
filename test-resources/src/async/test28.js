var t, z;
var x = Promise.resolve('foo');

x
  .then(function ff1(value) {
      TAJS_assertEquals(value, 'foo');
      return 'bar';
  })
  .then(function ff3(value) {
      t.foo // No error.
      z = {bar: 1};
      TAJS_assertEquals(value, 'bar');
      return 'lala'
  })
  .then(function ff5(value) {
      TAJS_assertEquals(value, 'lala');
      z.bar // Hit an error!
      TAJS_dumpValue('never printed');
  });

var y = Promise.resolve('bar');
y.then(function ff2(value) {
    TAJS_assertEquals(value, 'bar');
    t = {foo : 1};
    return 'baz';
}).then(function ff4(value) {
    TAJS_assertEquals(value, 'baz');
    z.bar; // No error.
    z = undefined;
});
