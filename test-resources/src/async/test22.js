function foo(param) {
    if (param)
        throw 'bar';
    else
        return 'foo'
}

try {
    foo(true);
} catch (e) {

}

var x = Promise.reject(true);
x.catch(function ff1(reason) {
      TAJS_assertEquals(reason, true);
      return false;
  })
  .then(foo)
  .then(function ff2(value) {
      TAJS_assertEquals(value, 'foo');
  }, function ff3() {
      TAJS_dumpValue("exceptional path printed")
  });
