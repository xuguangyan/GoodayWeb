/**
 * json对象转字符串形式
 */
function json2str(obj) {
    var str = "";
    if (typeof (obj) == 'undefined' || obj == null) {
        str = "";
    } else if (isArray(obj)) {
        str = jsonArray2str(obj);
    } else if (typeof (obj) == 'object') {
        var arr = [];
        var fmt = function(s) {
            if (typeof s == 'object' && s != null)
                return json2str(s);
            return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
        }
        for ( var i in obj) {
            arr.push("'" + i + "':" + fmt(obj[i]));
        }
        str = '{' + arr.join(',') + '}';
    } else {
        str = "'" + obj + "'";
    }
    return str;
}

/**
 * json对象数组转字符串形式
 */
function jsonArray2str(obj) {
    var str = "";
    if (typeof (obj) == 'undefined' || obj == null) {
        str = "";
    } else if (isArray(obj)) {
        str = "[";
        for ( var i = 0; i < obj.length; i++) {
            str += jsonArray2str(obj[i]) + ",";
        }
        str = str.substring(0, str.length - 1) + "]";
    } else if (typeof (obj) == 'object') {
        str = json2str(obj);
    } else {
        str = "'" + obj + "'";
    }
    return str;
}

/**
 * 判断对象是否是数组
 */
function isArray(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]';
}