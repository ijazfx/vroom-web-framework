/*
*  Copyright (C) 2008 Farrukh Ijaz, OpenKoncept Inc.
*
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

var _debug = true;
var _pageLoaded = null;

if (typeof VroomUtils == "undefined" || !VroomUtils) {
    var VroomUtils = {};

    VroomUtils.logError = function(msg) {
        if (_debug == true) {
            VroomUtils.popupError(msg);
        }
    }

    VroomUtils.popupError = function(msg) {
        var wndPopup = window.open("", "Error", "width=800,height=600,scrollbars=yes");
        wndPopup.document.writeln(msg);
    }

    VroomUtils.getElement = function(_elem_id) {
        var elem = document.getElementById(_elem_id);
        if (elem == null) {
            var elems = document.getElementsByName(_elem_id);
            if (elems != null && elems.length > 0) {
                elem = elems[0];
            }
        }
        return elem;
    };

    VroomUtils.triggerEvent = function(_elem_id, _event) {
        var elem = VroomUtils.getElement(_elem_id);
        if (elem == null) {
            return;
        }
        if (_event != null && _event.substring(0, 2) == 'on') {
            var stmt = null;
            if (_event == 'onchange' || _event == 'change') {
                stmt = 'elem.onchange()';
            } else {
                stmt = 'elem.' + _event.substring(2) + '()';
            }
            try {
                eval(stmt);
            } catch(e) {
                VroomUtils.logError(e.stack);
            }
        }
    };

    VroomUtils.replaceFirst = function (text, s, t) {
        if (text != null) {
            for (var x = 0; x < text.length; x++) {
                text = text.replace(s, t);
                break;
            }
        }
        return text;
    };

    VroomUtils.replaceAll = function (text, s, t) {
        if (text != null) {
            for (var x = 0; x < text.length; x++) {
                text = text.replace(s, t);
            }
        }
        return text;
    };

    VroomUtils.findForm = function(elem) {
        if (elem == null) return;
        if (elem.tagName == 'FORM') {
            return elem;
        }
        return VroomUtils.findForm(elem.parentNode);
    };

    VroomUtils.buildElementParams = function(_elem) {
        if (_elem == null) return null;
        var _id = (_elem.name == null || _elem.name == "") ? _elem.id : _elem.name;
        var _params = "";
        if (_id != null) {
            if (_elem.tagName == "INPUT") {
                if (_elem.type == "text" || _elem.type == "password" || _elem.type == "file") {
                    _params += _id + "=" + Utf8.encode(_elem.value) + "&";
                }
                if (_elem.type == "checkbox") {
                    if (_elem.checked) {
                        _params += _id + "=" + Utf8.encode(_elem.value) + "&";
                    } else {
                        _params += _id + "=&";
                    }
                }
                if (_elem.type == "radio") {
                    if (_elem.checked) {
                        _params += _id + "=" + Utf8.encode(_elem.value) + "&";
                    }
                }
            }
            if (_elem.tagName == "TEXTAREA") {
                _params += _id + "=" + Utf8.encode(_elem.innerHTML) + "&";
            }
            if (_elem.tagName == "SELECT") {
                var sel = _elem;
                if (sel.options != null && sel.options.length > 0) {
                    if (sel.selectedIndex != -1) {
                        _params += _id + "=" + Utf8.encode(sel.options[sel.selectedIndex].value) + "&";
                    }
                }
            }
        }
        return _params;
    }

    VroomUtils.buildParams = function(_id) {
        if (_id == null || _id == "") return;
        var _params = "";
        var _elem = VroomUtils.getElement(_id);
        var _form = VroomUtils.findForm(_elem);
        if (_form != null) {
            var _elements = _form.elements;
            for (var i = 0; i < _elements.length; i++) {
                _elem = _form.elements[i];
                if (i > 0) {
                    _params += '&';
                }
                _params += VroomUtils.buildElementParams(_elem);
            }
        } else {
            if (_elem != null) {
                _params = VroomUtils.buildElementParams(_elem);
            }
        }
        return _params;
    };

    VroomUtils.createInputField = function(_type, _name, _value) {
        var elem = null;
        try {
            elem = document.createElement('<INPUT type="' + _type + '" name="' + _name + '" value="' + _value + '" />');
        } catch(e) {
            elem = document.createElement("INPUT");
            elem.type = _type;
            elem.name = _name;
            elem.value = _value;
        }
        return elem;
    };

    VroomUtils.setupForm = function(_id, _action, _method, _beanClass, _var, _scope, _callerUri) {
        var form = document.forms[_id];
        if (form != null) {
            form.action = _action;
            form.appendChild(VroomUtils.createInputField("hidden", "v-rt", "submit"));
            form.appendChild(VroomUtils.createInputField("hidden", "v-id", _id));
            form.appendChild(VroomUtils.createInputField("hidden", "v-method", _method));
            form.appendChild(VroomUtils.createInputField("hidden", "v-bean-class", _beanClass));
            form.appendChild(VroomUtils.createInputField("hidden", "v-var", _var));
            form.appendChild(VroomUtils.createInputField("hidden", "v-scope", _scope));
            form.appendChild(VroomUtils.createInputField("hidden", "v-caller-uri", _callerUri));
        }
    };

    VroomUtils.callPostBack = function(_method) {
        if (_pageLoaded == null || _method == null) {
            setTimeout('VroomUtils.callPostBack(' + _method + ')', 100);
            return;
        } else {
            eval('_method()');
        }
    }

    VroomUtils.registerEvent = function(_id, _event, _method) {
        var _obj;
        _obj = VroomUtils.getElement(_id);
        if (_obj == null) {
            if (_id == 'document' || _id == 'window' || _id == 'navigator') {
                try {
                    _obj = eval(_id);
                } catch(e) {
                    VroomUtils.logError(e);
                }
            } else {
                var funcName = 'VroomUtils.registerEvent(\'' + _id + '\', \'' + _event + '\', ' + _method + ')';
                setTimeout(funcName, 100);
                return;
            }
        }
        if (_obj != null) {
            if (_obj.addEventListener != null) {
                var _ev = _event;
                if (_event.substring(0, 2) == "on") {
                    _ev = _event.substring(2);
                }
                try {
                    _obj.addEventListener(_ev, _method, false);
                } catch(e) {
                    VroomUtils.logError(e);
                }
            } else if (_obj.attachEvent != null) {
                try {
                    _obj.attachEvent(_event, _method);
                } catch(e) {
                    VroomUtils.logError(e);
                }
            }
        }
    };

//    VroomUtils.assignValuesToVariables = function(value, _output) {
    //        var sidx = 0;
    //        var _temp = value;
    //        while (sidx != -1) {
    //            sidx = _temp.indexOf("#{", 0);
    //            if (sidx >= 0) {
    //                eidx = _temp.indexOf("}", sidx);
    //                if (eidx > sidx) {
    //                    var _vn = _temp.substring(sidx + 2, eidx);
    //                    // var _vv = _output[_vn];
    //                    var _vv = _output[_vn];
    //                    var _vname = "#{" + _vn + "}";
    //                    if (_vv != null && _vv != "null" && _vv != "") {
    //                        _temp = VroomUtils.replaceAll(_temp, _vname, _vv);
    //                    } else {
    //                        _temp = VroomUtils.replaceAll(_temp, _vname, "${" + _vn + "}");
    //                    }
    //                }
    //            }
    //        }
    //        _temp = VroomUtils.replaceAll(_temp, "${", "#{");
    //        return _temp;
    //    };

    VroomUtils.assignValuesToVariables = function(value, _output) {
        var sidx = 0;
        var _temp = value;
        while (sidx != -1) {
            sidx = _temp.indexOf("#{", 0);
            if (sidx >= 0) {
                eidx = _temp.indexOf("}", sidx);
                if (eidx > sidx) {
                    var _vn = _temp.substring(sidx + 2, eidx);
                    // var _vv = _output[_vn];
                    var _vv = eval('_output.' + _vn);
                    if (typeof(_vv) == "object") {
                        _vv = JSON.stringify(_vv);
                    }
                    var _vname = "#{" + _vn + "}";
                    if (_vv != null && _vv != "null" && _vv != "") {
                        _temp = VroomUtils.replaceAll(_temp, _vname, _vv);
                    } else {
                        _temp = VroomUtils.replaceAll(_temp, _vname, "${" + _vn + "}");
                    }
                }
            }
        }
        _temp = VroomUtils.replaceAll(_temp, "${", "#{");
        return _temp;
    };

    VroomUtils.updateElement = function(elemId, jsonString) {
        // var o = eval('(' + value + ')');
        var o = JSON.parse(jsonString);
        var elem = document.getElementsByName(elemId);
        if (elem == null || elem.length == 0) {
            elem = VroomUtils.getElement(elemId);
            if (elem != null) {
                elem.innerHTML = Utf8.decode(o.value);
            }
            return;
        }
        for (var i = 0; i < elem.length; i++) {
            if (elem[i].tagName == "INPUT") {
                if (elem[i].type == "text" || elem[i].type == "password" || elem[i].type == "file") {
                    if (elem.length == 1) {
                        elem[i].value = Utf8.decode(o.value);
                    } else {
                        elem[i].value = Utf8.decode(o.array[i]);
                    }
                } else if (elem[i].type == "checkbox") {
                    if (elem.length == 1) {
                        if (elem[i].value == o.value) {
                            elem[i].checked = true;
                        }
                    } else if (o.array != null) {
                        for (var j = 0; j < o.array.length; j++) {
                            if (elem[i].value == o.array[j]) {
                                elem[i].checked = true;
                            }
                        }
                    }
                } else if (elem[i].type == "radio") {
                    if (elem[i].value == o.value) {
                        elem[i].checked = true;
                    }
                }
            } else if (elem[i].tagName == "TEXTAREA") {
                if (elem.length == 1) {
                    elem[i].value = Utf8.decode(o.value);
                } else {
                    elem[i].value = Utf8.decode(o.array[i]);
                }
            } else if (elem[i].tagName == "SELECT") {
                if (elem[i].multiple == true) {
                    for (var j = 0; j < elem[i].options.length; j++) {
                        var op = elem[i].options[j];
                        for (var k = 0; k < o.array.length; k++) {
                            if (op.value == o.array[k]) {
                                op.selected = true;
                            }
                        }
                    }
                } else {
                    for (var j = 0; j < elem[i].options.length; j++) {
                        var op = elem[i].options[j];
                        if (op.value == o.value) {
                            op.selected = true;
                        }
                    }
                }
            }
        }
    };

    VroomUtils.ajaxCall = function(_uri, _obj, _method, _sync, _beanClass, _var, _scope, _calls) {
        var vobj = new Vroom();
        var _callback = function(_output) {
            var calls = _calls.split('VRMB');
            for (var i = 0; i < calls.length - 1; i++) {
                var call = calls[i].split('|');
                if (call[0] == 'update') {
                    var id = call[1];
                    var tag = call[2];
                    var name = call[3];
                    var attribute = call[4];
                    var value = call[5];
                    if (value != null) {
                        value = VroomUtils.replaceAll(value, 'ESQUOTE', '\\\'');
                        value = VroomUtils.replaceAll(value, 'SQUOTE', '\'');
                        value = VroomUtils.replaceAll(value, 'CRLF', '\n');
                        value = VroomUtils.assignValuesToVariables(value, _output);
                    }
                    var obj = null;
                    if (id != null && id != 'null') {
                        obj = VroomUtils.getElement(id);
                    } else if (tag != null && tag != 'null') {
                        obj = VroomUtils.getElementByTagName(tag);
                    } else if (name != null && name != 'null') {
                        try {
                            obj = eval(name);
                        } catch(e) {
                            // nothing can be done...
                            VroomUtils.logError(e);
                        }
                    }
                    if (obj != null) {
                        var stmt = 'obj.' + attribute + '= value;';
                        eval(stmt);
                    }
                } else { // call type is "script"
                    var value = call[1];
                    if (value != null) {
                        value = VroomUtils.replaceAll(value, 'ESQUOTE', '\\\'');
                        value = VroomUtils.replaceAll(value, 'SQUOTE', '\'');
                        value = VroomUtils.replaceAll(value, 'CRLF', '\n');
                        value = VroomUtils.assignValuesToVariables(value, _output);
                        try {
                            eval(value);
                        } catch(e) {
                            // nothing can be done...
                            VroomUtils.logError(e);
                        }
                    }
                }
            }
        }
        if (_method == null || _method == "null" || _method == '') {
            _callback(null);
        } else {
            vobj.ajaxCall(_obj, _method, _sync, _beanClass, _var, _scope, _callback);
        }

    };

    VroomUtils.getElementByTagName = function(tagName) {
        var _obj = null;
        var _objs = document.getElementsByTagName(tagName);
        if (_objs != null && _objs.length > 0) {
            _obj = _objs[0];
        }
        return _obj;
    };

    VroomUtils.getElementsByTagName = function(tagName) {
        var _obj = null;
        var _objs = document.getElementsByTagName(tagName);
        if (_objs != null && _objs.length > 0) {
            _obj = _objs[0];
        }
        return _obj;
    };

    VroomUtils.getBodyObject = function() {
        var _obj = VroomUtils.getElementByTagName("body");
        if (_obj == null) {
            _obj = VroomUtils.getElementByTagName("frameset");
            if (_obj == null) {
                _obj = document;
            }
        }
        return _obj;
    };

    VroomUtils.generateRadioGroup = function(elemId, name, jsonString, cols, styleClass) {
        VroomUtils.generateRadioGroupEx(elemId, name, jsonString, 'label', 'value', cols, styleClass);
    };

    VroomUtils.generateRadioGroupEx = function(elemId, name, jsonString, label, value, cols, styleClass) {
        var elem = VroomUtils.getElement(elemId);
        if (elem != null) {
            var text = '<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\'>';
            var jo = JSON.parse(jsonString);
            text += '<tr>'
            var c = 0;
            for (var i = 0; i < jo.length; i++) {
                var id = 'rb' + elemId + name + Utf8.decode(jo[i][value]);
                id = VroomUtils.replaceAll(id, '-', '');
                if (styleClass != null) {
                    text += '<td><span class=\'' + styleClass + '\'><input id=\'' + id + '\' type=\'radio\' name=\'' + name + '\' value=\'' + Utf8.decode(jo[i][value]) + '\'/>' + Utf8.decode(jo[i][label]) + '</span></td>';
                } else {
                    text += '<td><span><input id=\'' + id + '\' type=\'radio\' name=\'' + name + '\' value=\'' + Utf8.decode(jo[i][value]) + '\'/>' + Utf8.decode(jo[i][label]) + '</span></td>';
                }
                c++;
                if (c == cols && i != jo.length) {
                    text += '</tr><tr>';
                    c = 0;
                }
            }
            text += '</tr>';
            text += '</table>';
            elem.innerHTML = text;
        }
    };

    VroomUtils.generateCheckBoxGroup = function(elemId, name, jsonString, cols, styleClass) {
        VroomUtils.generateCheckBoxGroupEx(elemId, name, jsonString, 'label', 'value', cols, styleClass);
    };

    VroomUtils.generateCheckBoxGroupEx = function(elemId, name, jsonString, label, value, cols, styleClass) {
        var elem = VroomUtils.getElement(elemId);
        if (elem != null) {
            var text = '<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\'>';
            var jo = JSON.parse(jsonString);
            text += '<tr>'
            var c = 0;
            for (var i = 0; i < jo.length; i++) {
                var id = 'cb' + elemId + name + Utf8.decode(jo[i][value]);
                id = VroomUtils.replaceAll(id, '-', '');
                if (styleClass != null) {
                    text += '<td><span class=\'' + styleClass + '\'><input id=\'' + id + '\' type=\'checkbox\' name=\'' + name + '\' value=\'' + Utf8.decode(jo[i][value]) + '\'/>' + Utf8.decode(jo[i][label]) + '</span></td>';
                } else {
                    text += '<td><span><input id=\'' + id + '\' type=\'checkbox\' name=\'' + name + '\' value=\'' + Utf8.decode(jo[i][value]) + '\'/>' + Utf8.decode(jo[i][label]) + '</span></td>';
                }
                c++;
                if (c == cols && i != jo.length) {
                    text += '</tr><tr>';
                    c = 0;
                }
            }
            text += '</tr>';
            text += '</table>';
            elem.innerHTML = text;
        }
    };

    VroomUtils.populateSelect = function(elemId, jsonString) {
        VroomUtils.populateSelectEx(elemId, jsonString, 'label', 'value');
    };

    VroomUtils.populateSelectEx = function(elemId, jsonString, label, value) {
        var elem = VroomUtils.getElement(elemId);
        if (elem != null && elem.tagName == "SELECT") {
            var jo = JSON.parse(jsonString);
            for (var i = 0; i < jo.length; i++) {
                var op = new Option(Utf8.decode(jo[i][label]), Utf8.decode(jo[i][value]));
                elem.options.add(op);
            }
        }
    };
}

function Vroom() {
    this.contextPath = '__CONTEXT_PATH__';
    this.servletPath = '__SERVLET_PATH__';
    this.sessionId = '__SESSION_ID__';
    this.requestPath = this.contextPath + this.servletPath + ';jsessionid=' + this.sessionId + '?';
    this.ajaxCall = function(_obj, _method, _sync, _beanClass, _var, _scope, _callback) {
        var _id = _obj.id;
        var url = this.requestPath
                + 'v-rt=invoke&v-id=' + _id + '&v-method=' + _method
                + '&v-bean-class=' + _beanClass + '&v-var=' + _var + '&v-scope=' + _scope;
        var xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("MSXML2.XMLHTTP.3.0");
        var async = !_sync;
        xhr.open("POST", url, async);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function() {
            if ((xhr.readyState == 4) && (xhr.status == 200)) {
                // var jsonObject = eval("(" + xhr.responseText + ")");
                var jsonObject = JSON.parse(xhr.responseText);
                _callback(jsonObject);
                xhr.abort();
                _pageLoaded = true;
            } else if ((xhr.readyState == 4) && (xhr.status != 200)) {
                VroomUtils.logError(xhr.responseText);
                xhr.abort();
            }
        }
        xhr.send(VroomUtils.buildParams(_id));
        return url;
    };
}

// Following code is part of json2.js opensource library
// copied from http://www.json.org/json2.js

/*
    http://www.JSON.org/json2.js
    2008-07-15

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html

    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the object holding the key.

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array, then it will be used to
            select the members to be serialized. It filters the results such
            that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.

    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.
*/

/*jslint evil: true */

/*global JSON */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", call,
    charCodeAt, getUTCDate, getUTCFullYear, getUTCHours, getUTCMinutes,
    getUTCMonth, getUTCSeconds, hasOwnProperty, join, lastIndex, length,
    parse, propertyIsEnumerable, prototype, push, replace, slice, stringify,
    test, toJSON, toString
*/

if (!this.JSON) {

    // Create a JSON object only if one does not already exist. We create the
    // object in a closure to avoid creating global variables.

    JSON = function () {

        function f(n) {
            // Format integers to have at least two digits.
            return n < 10 ? '0' + n : n;
        }

        Date.prototype.toJSON = function (key) {

            return this.getUTCFullYear() + '-' +
                   f(this.getUTCMonth() + 1) + '-' +
                   f(this.getUTCDate()) + 'T' +
                   f(this.getUTCHours()) + ':' +
                   f(this.getUTCMinutes()) + ':' +
                   f(this.getUTCSeconds()) + 'Z';
        };

        String.prototype.toJSON =
        Number.prototype.toJSON =
        Boolean.prototype.toJSON = function (key) {
            return this.valueOf();
        };

        var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
                escapeable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
                gap,
                indent,
                meta = {    // table of character substitutions
                    '\b': '\\b',
                    '\t': '\\t',
                    '\n': '\\n',
                    '\f': '\\f',
                    '\r': '\\r',
                    '"' : '\\"',
                    '\\': '\\\\'
                },
                rep;


        function quote(string) {

            // If the string contains no control characters, no quote characters, and no
            // backslash characters, then we can safely slap some quotes around it.
            // Otherwise we must also replace the offending characters with safe escape
            // sequences.

            escapeable.lastIndex = 0;
            return escapeable.test(string) ?
                   '"' + string.replace(escapeable, function (a) {
                       var c = meta[a];
                       if (typeof c === 'string') {
                           return c;
                       }
                       return '\\u' + ('0000' +
                                       (+(a.charCodeAt(0))).toString(16)).slice(-4);
                   }) + '"' :
                   '"' + string + '"';
        }


        function str(key, holder) {

            // Produce a string from holder[key].

            var i,          // The loop counter.
                    k,          // The member key.
                    v,          // The member value.
                    length,
                    mind = gap,
                    partial,
                    value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

            if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
                value = value.toJSON(key);
            }

// If we were called with a replacer function, then call the replacer to
            // obtain a replacement value.

            if (typeof rep === 'function') {
                value = rep.call(holder, key, value);
            }

// What happens next depends on the value's type.

            switch (typeof value) {
                case 'string':
                    return quote(value);

                case 'number':

                // JSON numbers must be finite. Encode non-finite numbers as null.

                    return isFinite(value) ? String(value) : 'null';

                case 'boolean':
                case 'null':

                // If the value is a boolean or null, convert it to a string. Note:
                // typeof null does not produce 'null'. The case is included here in
                // the remote chance that this gets fixed someday.

                    return String(value);

                // If the type is 'object', we might be dealing with an object or an array or
                // null.

                case 'object':

                // Due to a specification blunder in ECMAScript, typeof null is 'object',
                // so watch out for that case.

                    if (!value) {
                        return 'null';
                    }

                // Make an array to hold the partial results of stringifying this object value.

                    gap += indent;
                    partial = [];

                // If the object has a dontEnum length property, we'll treat it as an array.

                    if (typeof value.length === 'number' &&
                        !(value.propertyIsEnumerable('length'))) {

                        // The object is an array. Stringify every element. Use null as a placeholder
                        // for non-JSON values.

                        length = value.length;
                        for (i = 0; i < length; i += 1) {
                            partial[i] = str(i, value) || 'null';
                        }

// Join all of the elements together, separated with commas, and wrap them in
                        // brackets.

                        v = partial.length === 0 ? '[]' :
                            gap ? '[\n' + gap +
                                  partial.join(',\n' + gap) + '\n' +
                                  mind + ']' :
                            '[' + partial.join(',') + ']';
                        gap = mind;
                        return v;
                    }

                // If the replacer is an array, use it to select the members to be stringified.

                    if (rep && typeof rep === 'object') {
                        length = rep.length;
                        for (i = 0; i < length; i += 1) {
                            k = rep[i];
                            if (typeof k === 'string') {
                                v = str(k, value);
                                if (v) {
                                    partial.push(quote(k) + (gap ? ': ' : ':') + v);
                                }
                            }
                        }
                    } else {

                        // Otherwise, iterate through all of the keys in the object.

                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = str(k, value);
                                if (v) {
                                    partial.push(quote(k) + (gap ? ': ' : ':') + v);
                                }
                            }
                        }
                    }

                // Join all of the member texts together, separated with commas,
                // and wrap them in braces.

                    v = partial.length === 0 ? '{}' :
                        gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                              mind + '}' : '{' + partial.join(',') + '}';
                    gap = mind;
                    return v;
            }
        }

// Return the JSON object containing the stringify and parse methods.

        return {
            stringify: function (value, replacer, space) {

                // The stringify method takes a value and an optional replacer, and an optional
                // space parameter, and returns a JSON text. The replacer can be a function
                // that can replace values, or an array of strings that will select the keys.
                // A default replacer method can be provided. Use of the space parameter can
                // produce text that is more easily readable.

                var i;
                gap = '';
                indent = '';

// If the space parameter is a number, make an indent string containing that
                // many spaces.

                if (typeof space === 'number') {
                    for (i = 0; i < space; i += 1) {
                        indent += ' ';
                    }

// If the space parameter is a string, it will be used as the indent string.

                } else if (typeof space === 'string') {
                    indent = space;
                }

// If there is a replacer, it must be a function or an array.
                // Otherwise, throw an error.

                rep = replacer;
                if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                    throw new Error('JSON.stringify');
                }

// Make a fake root object containing our value under the key of ''.
                // Return the result of stringifying the value.

                return str('', {'': value});
            },


            parse: function (text, reviver) {

                // The parse method takes a text and an optional reviver function, and returns
                // a JavaScript value if the text is a valid JSON text.

                var j;

                function walk(holder, key) {

                    // The walk method is used to recursively walk the resulting structure so
                    // that modifications can be made.

                    var k, v, value = holder[key];
                    if (value && typeof value === 'object') {
                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = walk(value, k);
                                if (v !== undefined) {
                                    value[k] = v;
                                } else {
                                    delete value[k];
                                }
                            }
                        }
                    }
                    return reviver.call(holder, key, value);
                }


// Parsing happens in four stages. In the first stage, we replace certain
                // Unicode characters with escape sequences. JavaScript handles many characters
                // incorrectly, either silently deleting them, or treating them as line endings.

                cx.lastIndex = 0;
                if (cx.test(text)) {
                    text = text.replace(cx, function (a) {
                        return '\\u' + ('0000' +
                                        (+(a.charCodeAt(0))).toString(16)).slice(-4);
                    });
                }

// In the second stage, we run the text against regular expressions that look
                // for non-JSON patterns. We are especially concerned with '()' and 'new'
                // because they can cause invocation, and '=' because it can cause mutation.
                // But just to be safe, we want to reject all unexpected forms.

                // We split the second stage into 4 regexp operations in order to work around
                // crippling inefficiencies in IE's and Safari's regexp engines. First we
                // replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
                // replace all simple value tokens with ']' characters. Third, we delete all
                // open brackets that follow a colon or comma or that begin the text. Finally,
                // we look to see that the remaining characters are only whitespace or ']' or
                // ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

                if (/^[\],:{}\s]*$/.
                        test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
                        replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
                        replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

                    // In the third stage we use the eval function to compile the text into a
                    // JavaScript structure. The '{' operator is subject to a syntactic ambiguity
                    // in JavaScript: it can begin a block or an object literal. We wrap the text
                    // in parens to eliminate the ambiguity.

                    j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
                    // each name/value pair to a reviver function for possible transformation.

                    return typeof reviver === 'function' ?
                           walk({'': j}, '') : j;
                }

// If the text is not JSON parseable, then a SyntaxError is thrown.

                throw new SyntaxError('JSON.parse');
            }
        };
    }();
}

/**
 *
 *  UTF-8 data encode / decode
 *  http://www.webtoolkit.info/
 *
 **/

var Utf8 = {

    // public method for url encoding
    encode : function (string) {
        if (string == null) return null;
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    // public method for url decoding
    decode : function (utftext) {
        if (utftext == null) return null;
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}