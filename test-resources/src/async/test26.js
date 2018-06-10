var x = Promise.resolve('foo');
var z;
var y = {
    then: function ff2(res) {
        TAJS_dumpValue('thenable executed');
        z = {t : 1};
        res('thenable');
    }
};

x
  .then(function ff1(value) {
      TAJS_assertEquals(value, 'foo');
      return Promise.resolve(y);
  })
  .then(function ff3(value) {
      z.t // no error;
      TAJS_assertEquals(value, 'thenable');
  });
