// version 20170715
// 需要JQuery.js


// 全局常量对象 ----------------------------------------------------------------------------------------------------------
$.mrathena = {
	type: {
		undefined: "undefined",
		function: "function",
		object: "object",
		boolean: "boolean",
		string: "string",
		number: "number"
	}
};


// 补充JavaScript
String.prototype.startWith = function(str) {
	var reg = new RegExp("^" + str);
	return reg.test(this);
};
String.prototype.endWith = function(str) {
	var reg = new RegExp(str + "$");
	return reg.test(this);
};
String.prototype.isEmpty = function() {
	return "" === this;
};

// 补充JQuery
// undefined --> null --> empty
$.isUndefined = function() {
	if (arguments.length === 0) {
		throw "$.isUndefined() need at least one argument";
	}
	for (var i = 0; i < arguments.length; i++) {
		if (typeof arguments[i] !== $.mrathena.type.undefined) {
			return false;
		}
	}
	return true;
};
$.isNotUndefined = function() {
	if (arguments.length === 0) {
		throw "$.isNotUndefined() need at least one argument";
	}
	for (var i = 0; i < arguments.length; i++) {
		if (typeof arguments[i] === $.mrathena.type.undefined) {
			return false;
		}
	}
	return true;
};
// 自己斟酌是否需要考虑参数为undefined的情况
$.isNull = function() {
	if (arguments.length === 0) {
		throw "$.isNull() need at least one argument";
	}
	for (var i = 0; i < arguments.length; i++) {
		if (arguments[i] !== null) {
			return false;
		}
	}
	return true;
};
// 自己斟酌是否需要考虑参数为undefined的情况
$.isNotNull = function() {
	if (arguments.length === 0) {
		throw "$.isNotNull() need at least one argument";
	}
	for (var i = 0; i < arguments.length; i++) {
		if (arguments[i] === null) {
			return false;
		}
	}
	return true;
};
// $.isEmptyObject();
$.isNone = function() {
	if (arguments.length !== 1) {
		throw "$.isNone() need and just need one argument";
	}
	for (var i = 0; i < arguments.length; i++) {
		// 这里特意加上了 arguments[i] === "undefined", 有时候给input赋值会有这种情况
		var isNone = typeof arguments[i] === $.mrathena.type.undefined || arguments[i] === null
			|| arguments[i] === "" || arguments[i] === "undefined";
		if (!isNone) {
			return false;
		}
	}
	return true;
};

$.copy = function(object) {
	return $.extend(true, {}, object);
};

$.getDate = function(date) {
	var datetime = new Date();
	if (!$.isNone(date)) {
		if (date instanceof Date) {
			datetime = date;
		} else {
			throw "$.getDate() need a Date argument";
		}
	}
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1;
	month = Number(month) < 10 ? ("0" + month) : month;
	var day = datetime.getDate();
	day = Number(day) < 10 ? ("0" + day) : day;
	return year + "-" + month + "-" + day;
};
$.getTime = function(date) {
	var datetime = new Date();
	if (!$.isNone(date)) {
		if (date instanceof Date) {
			datetime = date;
		} else {
			throw "$.getDate() need a Date argument";
		}
	}
	var hours = datetime.getHours();
	hours = Number(hours) < 10 ? ("0" + hours) : hours;
	var minute = datetime.getMinutes();
	minute = Number(minute) < 10 ? ("0" + minute) : minute;
	var second = datetime.getSeconds();
	second = Number(second) < 10 ? ("0" + second) : second;
	return hours + ":" + minute + ":" + second;
};
$.getDateTime = function(date) {
	var datetime = new Date();
	if (!$.isNone(date)) {
		if (date instanceof Date) {
			datetime = date;
		} else {
			throw "$.getDate() need a Date argument";
		}
	}
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1;
	month = Number(month) < 10 ? ("0" + month) : month;
	var day = datetime.getDate();
	day = Number(day) < 10 ? ("0" + day) : day;
	var hours = datetime.getHours();
	hours = Number(hours) < 10 ? ("0" + hours) : hours;
	var minute = datetime.getMinutes();
	minute = Number(minute) < 10 ? ("0" + minute) : minute;
	var second = datetime.getSeconds();
	second = Number(second) < 10 ? ("0" + second) : second;
	return year + "-" + month + "-" + day + " " + hours + ":" + minute + ":" + second;
};

// time: d10:10天, h12:12小时, m20:20分钟, s30:30秒
$.setCookie = function(cookieName, cookieValue, time) {
	var cookie, expire = new Date();
	if ($.isNone(time) || time === 0) {
		cookie = cookieName + "=" + encodeURIComponent(encodeURIComponent(cookieValue));
	} else {
		// time 最后需要转换为秒
		var reg = new RegExp("^[dhms][1-9]+[0-9]*$");
		if (reg.test(time)) {
			var type = time.substr(0, 1);
			var value = time.substr(1);
			if ("s" === type) {
				time = value * 1000;
			} else if ("m" === type) {
				time = value * 60 * 1000;
			} else if ("h" === type) {
				time = value * 60 * 60 *1000;
			} else if ("d" === type) {
				time = value * 24 * 60 * 60 * 1000;
			} else {
				time = 0;
			}
		} else {
			throw "$.setCookie() argument time need 0 or one of [d, h, m, s] concat a number";
		}
		expire.setTime(expire.getTime() + time);
		cookie = cookieName + "=" + encodeURIComponent(encodeURIComponent(cookieValue)) + ";expires=" + expire.toGMTString();
	}
	document.cookie = cookie;
};
$.getCookie = function(cookieName, isOneTime) {
	var result, reg = new RegExp("(^| )" + cookieName + "=([^;]*)(;|$)");
	var array = document.cookie.match(reg);
	if (array !== null) {
		result = decodeURIComponent(decodeURIComponent(array[2]));
	} else {
		result = null;
	}
	if (!$.isNone(isOneTime) && isOneTime === true) {
		$.delCookie(cookieName);
	}
	return result;
};
$.delCookie = function(cookieName) {
	var cookie, expire = new Date();
	expire.setTime(expire.getTime() - 1);
	var cookieValue = $.getCookie(cookieName);
	if(cookieValue !== null) {
		cookie = cookieName + "=" + encodeURIComponent(encodeURIComponent(cookieValue)) + ";expires=" + expire.toGMTString();
		document.cookie = cookie;
	}
};

$.ajaxJson = function (url, method, data, isAsync, successCall, errorCall, beforeCall, afterCall) {
	return $.ajax({
		// contentType: 告诉服务器，我要发什么类型的数据
		// dataType：告诉服务器，我要想什么类型的数据，如果没有指定，那么会自动推断是返回 XML，还是JSON，还是script，还是String。
		// Accept：告诉服务器，能接受什么类型。
		headers: {
			Accept: "application/json;charset=UTF-8"
		},
		/*contentType: "application/json;charset=UTF-8",*/
		dataType: "json",
		url: url,
		type: method,
		data: data,
		// timeout: 1000,
		async: isAsync,
		beforeSend: beforeCall, // XMLHttpRequest
		complete: afterCall, // XMLHttpRequest, status
		success: successCall, // response
		error: function(XMLHttpRequest, type, message) {
			console.error("Ajax Error, URL:" + url + ", Method:"+ method +", Type:" + type + ", Message:" + message);
			errorCall(XMLHttpRequest, type, message);
		}
	});
};
$.getJson = function(url, isAsync, successCall, errorCall) {
	return $.ajaxJson(url, "get", {}, isAsync, successCall, errorCall, function() {}, function() {});
};
$.postJson = function(url, data, isAsync, successCall, errorCall) {
	return $.ajaxJson(url, "post", data, isAsync, successCall, errorCall, function() {}, function() {});
};

$.serialize = function(object) {
	return encodeURIComponent(encodeURIComponent(JSON.stringify(object)));
};
$.unserialize = function(string) {
	return JSON.parse(decodeURIComponent(decodeURIComponent(string)));
};
// 获取url中?后面的参数
$.getUrlParam = function(parameterName) {
	var reg = new RegExp("(^|&)" + parameterName + "=([^&]*)(&|$)", "i");
	var array = window.location.search.substr(1).match(reg);
	if (array !== null) {
		return decodeURIComponent(decodeURIComponent(array[2]));
	}
	return null;
};
$.getUrlWithObject = function(url, object) {
	url += (url.indexOf('?') !== -1) ? "&" : "?" + "OBJECT=" + encodeURIComponent(encodeURIComponent(JSON.stringify(object)));
	return url;
};
$.toUrlWithObject = function(url, object) {
	window.location.href = $.getUrlWithObject(url, object);
};
$.getObjectFromUrl = function(url) {
	if (url.indexOf("OBJECT=") === -1) {
		throw "Not found parameter OBJECT in url parames";
	}
	var string = $.getUrlParam("OBJECT");
	return JSON.parse(decodeURIComponent(decodeURIComponent(string)));
};

$.log = function() {
	console.log.apply(console, arguments);
};



