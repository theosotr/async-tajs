var x = Promise.resolve('foo');
x
  .then(function ff1(value) {
      TAJS_assertEquals(value, 'foo');
      // The next promise is resolved based on
      // this created promise.
      return Promise.resolve('bar');
  })
  .then(function ff2(value) {
      TAJS_assertEquals(value, 'bar');
      return Promise.reject('rejected')
  })
  .catch(function ff3(reason) {
      TAJS_assertEquals(reason, 'rejected');
      return new Promise(function (res) {
          res('foo')
      })
  })
  .then(function ff4(value) {
      TAJS_assertEquals(value, 'foo');
      return new Promise(function (res, rej) {
          rej('rejected again');
      })
  })
  .catch(function ff5(reason) {
      TAJS_assertEquals(reason, 'rejected again');
      return new Promise(function () {

      })
  })
  .then(function ff6() {
      TAJS_dumpValue('never printed')
  },
  function ff7() {
      TAJS_dumpValue('never printed')
  });
