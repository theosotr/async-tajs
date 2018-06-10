/* Taken from https://scotch.io/tutorials/javascript-promises-for-dummies */
var isMomHappy = true;

// Promise
var willIGetNewPhone = new Promise(
    function (resolve, reject) {
        var phone = {
            brand: 'Samsung',
            color: 'black'
        };
        if (isMomHappy) {
            resolve(phone); // fulfilled
        } else {
            var reason = new Error('mom is not happy');
            reject(reason); // reject
        }

    }
);

var showOff = function showOff(phone) {
    phone.price = 10;
    return phone;
};

// call our promise
var askMom = function () {
    willIGetNewPhone
        .then(showOff)
        .then(function ff1(value) {
            TAJS_assertEquals(value.brand, 'Samsung');
            TAJS_assertEquals(value.color, 'black');
            TAJS_dumpValue(value.price);
        })
        .catch(function ff2() {
            TAJS_dumpValue('never printed');
        });
};
askMom();
