function decodeURIComponent(x) {return x}

var clicky_obj = clicky_obj || (function () {
    this.get_cookie = function (name) {
        var ca = document.cookie.split(';');
        for (var i = 0, l = ca.length; i < l; i++) {
            if (eval("ca[i].match(/\\b" + name + "=/)"))
                return decodeURIComponent(ca[i].split('=')[1]);
        }
        return '';
    };
})

var _self = new clicky_obj

_self.get_cookie('clicky_olark')
_self.get_cookie('clicky_olark');
_self.get_cookie('no_tracky')
_self.get_cookie('_jsuid')
_self.get_cookie('_jsuid')