
var t;
function foo() {
    if (TAJS_make('AnyBool')) {
        return Promise.resolve('foo').then(
            function ff2() {
                t = {bar: 1};
                return 'foo';
            }
        ).then(function ff4() {
            return 'foo';
        });
    } else {
        return Promise.resolve({
            then: function ff3(res) {
                TAJS_dumpValue('thenable exec');
                t = {bar : 1};
                res('bar');
            }
        });
    }
}


var x = Promise.resolve('bar');
x
  .then(function ff1(value) {
      TAJS_assertEquals(value, 'bar');
      return foo();
  })
  .then(function ff5(value) {
      t.bar; // no error;
      TAJS_dumpValue(value);
  });
