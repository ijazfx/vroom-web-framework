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

var _debug = false;
var _pageLoaded = false;

if (typeof VroomUtils == "undefined" || !VroomUtils) {
    var VroomUtils = {};

    VroomUtils.logError = function(msg) {
        if (_debug == true) {
            if (console !== null && console.error !== null) {
                console.error(msg);
            } else {
                alert(msg);
            }
        }
    }

    VroomUtils.getElementByName = function(elemName) {
        var elem = null;
        var elems = document.getElementsByName(elemName);
        if (elems != null && elems.length > 0) {
            elem = elems[0];
        }
        return elem;
    };

    VroomUtils.getElementById = function(elemName) {
        var elem = document.getElementById(elemName);
        return elem;
    };

    VroomUtils.getWoodstockElement = function(elem_id) {
        var elem = null;
        // check for label...
        var _woodstockId = elem_id + "_valueContainer";
        elem = VroomUtils.getElementById(_woodstockId);
        if (elem == null) {
            elem = VroomUtils.getElementByName(_woodstockId);
        }
        if (elem == null) { // textbox, textarea
            _woodstockId = elem_id + "_field";
            elem = VroomUtils.getElementById(_woodstockId);
            if (elem == null) {
                elem = VroomUtils.getElementByName(_woodstockId);
            }
        }
        if (elem == null) { // fileupload
            _woodstockId = elem_id + "_com.sun.webui.jsf.upload";
            elem = VroomUtils.getElementById(_woodstockId);
            if (elem == null) {
                elem = VroomUtils.getElementByName(_woodstockId);
            }
        }
        if (elem == null) { // radio button
            _woodstockId = elem_id + "_rb";
            elem = VroomUtils.getElementById(_woodstockId);
            if (elem == null) {
                elem = VroomUtils.getElementByName(_woodstockId);
            }
        }
        if (elem == null) { // checkbox
            _woodstockId = elem_id + "_cb";
            elem = VroomUtils.getElementById(_woodstockId);
            if (elem == null) {
                elem = VroomUtils.getElementByName(_woodstockId);
            }
        }
        if (elem == null) { // select
            _woodstockId = elem_id + "_list";
            elem = VroomUtils.getElementById(_woodstockId);
            if (elem == null) {
                elem = VroomUtils.getElementByName(_woodstockId);
            }
        }
        return elem;
    }

    VroomUtils.getElement = function(elem_id) {
        var elem = null;
        if (elem == null) {
            elem = VroomUtils.getElementById(elem_id);
            if (elem == null) {
                elem = VroomUtils.getElementByName(elem_id);
            }
        }
        // uncomment following to enable woodstock control detection...
        //        if(elem === null) {
        //            elem = VroomUtils.getWoodstockElement(elem_id);
        //        }
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
                VroomUtils.logError(e);
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
                    _params += _id + "=" + encodeURIComponent(_elem.value) + "&";
                }
                if (_elem.type == "checkbox") {
                    if (_elem.checked) {
                        _params += _id + "=" + encodeURIComponent(_elem.value) + "&";
                    } else {
                        _params += _id + "=&";
                    }
                }
                if (_elem.type == "radio") {
                    if (_elem.checked) {
                        _params += _id + "=" + encodeURIComponent(_elem.value) + "&";
                    }
                }
            }
            if (_elem.tagName == "TEXTAREA") {
                _params += _id + "=" + encodeURIComponent(_elem.innerHTML) + "&";
            }
            if (_elem.tagName == "SELECT") {
                var sel = _elem;
                if (sel.options != null && sel.options.length > 0) {
                    if (sel.selectedIndex != -1) {
                        _params += _id + "=" + encodeURIComponent(sel.options[sel.selectedIndex].value) + "&";
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
        if (_method !== null) {
            eval(_method + '()');
        }
    }

    VroomUtils.registerEvent = function(_id, _event, _method) {
        var _obj = null;
        if (_id === 'document' || _id === 'window' || _id === 'navigator') {
            _obj = eval(_id);
        } else {
            _obj = VroomUtils.getElement(_id);
            if (_obj === null) {
                setTimeout(function() {
                    VroomUtils.registerEvent(_id, _event, _method);
                }, 20);
                return;
            }
        }
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
    };

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
        var o = JSON.parse(jsonString);
        var elem = document.getElementsByName(elemId);
        if (elem == null || elem.length == 0) {
            elem = VroomUtils.getElement(elemId);
            if (elem != null) {
                elem.innerHTML = decodeURIComponent(o.value);
            }
            return;
        }
        for (var i = 0; i < elem.length; i++) {
            if (elem[i].tagName == "INPUT") {
                if (elem[i].type == "text" || elem[i].type == "password" || elem[i].type == "file") {
                    if (elem.length == 1) {
                        elem[i].value = decodeURIComponent(o.value);
                    } else {
                        elem[i].value = decodeURIComponent(o.array[i]);
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
                    elem[i].value = decodeURIComponent(o.value);
                } else {
                    elem[i].value = decodeURIComponent(o.array[i]);
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
                        if (value.startsWith("url[")) {
                            var url = value.substring(4, value.length - 1);
                            new Ajax.Request(url, {
                                asynchronous: false,
                                evalJS: false,
                                evalJSON: false,
                                onSuccess: function(transport) {
                                    value = transport.responseText;
                                }
                            });
                        } else {
                            value = VroomUtils.replaceAll(value, '&#x5C;&#x27;', '\\\'');
                            value = VroomUtils.replaceAll(value, '&#x27;', '\'');
                            value = VroomUtils.replaceAll(value, 'CRLF', '\n');
                        }
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
                        if (value.startsWith("url[")) {
                            var url = value.substring(4, value.length - 1);
                            new Ajax.Request(url, {
                                asynchronous: false,
                                evalJS: false,
                                evalJSON: false,
                                onSuccess: function(transport) {
                                    value = transport.responseText;
                                }
                            });
                        } else {
                            value = VroomUtils.replaceAll(value, '&#x5C;&#x27;', '\\\'');
                            value = VroomUtils.replaceAll(value, '&#x27;', '\'');
                            value = VroomUtils.replaceAll(value, 'CRLF', '\n');
                        }
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
                var id = 'rb' + elemId + name + decodeURIComponent(jo[i][value]);
                id = VroomUtils.replaceAll(id, '-', '');
                if (styleClass != null) {
                    text += '<td><span class=\'' + styleClass + '\'><input id=\'' + id + '\' type=\'radio\' name=\'' + name + '\' value=\'' + decodeURIComponent(jo[i][value]) + '\'/>' + decodeURIComponent(jo[i][label]) + '</span></td>';
                } else {
                    text += '<td><span><input id=\'' + id + '\' type=\'radio\' name=\'' + name + '\' value=\'' + decodeURIComponent(jo[i][value]) + '\'/>' + decodeURIComponent(jo[i][label]) + '</span></td>';
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
                var id = 'cb' + elemId + name + decodeURIComponent(jo[i][value]);
                id = VroomUtils.replaceAll(id, '-', '');
                if (styleClass != null) {
                    text += '<td><span class=\'' + styleClass + '\'><input id=\'' + id + '\' type=\'checkbox\' name=\'' + name + '\' value=\'' + decodeURIComponent(jo[i][value]) + '\'/>' + decodeURIComponent(jo[i][label]) + '</span></td>';
                } else {
                    text += '<td><span><input id=\'' + id + '\' type=\'checkbox\' name=\'' + name + '\' value=\'' + decodeURIComponent(jo[i][value]) + '\'/>' + decodeURIComponent(jo[i][label]) + '</span></td>';
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
                var op = new Option(decodeURIComponent(jo[i][label]), decodeURIComponent(jo[i][value]));
                elem.options.add(op);
            }
        }
    };

    VroomUtils.generateUrl = function(_method, _beanClass, _var, _scope) {
        var _contextPath = '__CONTEXT_PATH__';
        var _servletPath = '__SERVLET_PATH__';
        var _sessionId = '__SESSION_ID__';
        var _requestPath = _contextPath + _servletPath + ';jsessionid=' + _sessionId + '?';
        var _url = _requestPath
                + 'v-rt=invoke&v-method=' + _method
                + '&v-bean-class=' + _beanClass + '&v-var=' + _var + '&v-scope=' + _scope;
        return _url;
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
        new Ajax.Request(url, {
            asynchronous: !_sync,
            parameters: VroomUtils.buildParams(_id),
            onSuccess: function(transport) {
                _callback(transport.responseJSON);
                _pageLoaded = true;
            }
        });
        return url;
    };
}

// Following code is part of json2.js opensource library
// copied from http://www.json.org/json2.js
// comments have been removed to skim the script. Ror doc visit the above url.

if (!this.JSON) {

    JSON = function () {

        function f(n) {
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

            var i,          // The loop counter.
                    k,          // The member key.
                    v,          // The member value.
                    length,
                    mind = gap,
                    partial,
                    value = holder[key];

            if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
                value = value.toJSON(key);
            }

            if (typeof rep === 'function') {
                value = rep.call(holder, key, value);
            }

            switch (typeof value) {
                case 'string':
                    return quote(value);

                case 'number':

                    return isFinite(value) ? String(value) : 'null';

                case 'boolean':
                case 'null':

                    return String(value);

                case 'object':

                    if (!value) {
                        return 'null';
                    }

                    gap += indent;
                    partial = [];

                    if (typeof value.length === 'number' &&
                        !(value.propertyIsEnumerable('length'))) {

                        length = value.length;
                        for (i = 0; i < length; i += 1) {
                            partial[i] = str(i, value) || 'null';
                        }

                        v = partial.length === 0 ? '[]' :
                            gap ? '[\n' + gap +
                                  partial.join(',\n' + gap) + '\n' +
                                  mind + ']' :
                            '[' + partial.join(',') + ']';
                        gap = mind;
                        return v;
                    }

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

                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = str(k, value);
                                if (v) {
                                    partial.push(quote(k) + (gap ? ': ' : ':') + v);
                                }
                            }
                        }
                    }

                    v = partial.length === 0 ? '{}' :
                        gap ? '{\n' + gap + partial.join(',\n' + gap) + '\n' +
                              mind + '}' : '{' + partial.join(',') + '}';
                    gap = mind;
                    return v;
            }
        }

        return {
            stringify: function (value, replacer, space) {

                var i;
                gap = '';
                indent = '';

                if (typeof space === 'number') {
                    for (i = 0; i < space; i += 1) {
                        indent += ' ';
                    }

                } else if (typeof space === 'string') {
                    indent = space;
                }

                rep = replacer;
                if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                     typeof replacer.length !== 'number')) {
                    throw new Error('JSON.stringify');
                }

                return str('', {'': value});
            },


            parse: function (text, reviver) {

                var j;

                function walk(holder, key) {

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

                cx.lastIndex = 0;
                if (cx.test(text)) {
                    text = text.replace(cx, function (a) {
                        return '\\u' + ('0000' +
                                        (+(a.charCodeAt(0))).toString(16)).slice(-4);
                    });
                }

                if (/^[\],:{}\s]*$/.
                        test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
                        replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
                        replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

                    j = eval('(' + text + ')');

                    return typeof reviver === 'function' ?
                           walk({'': j}, '') : j;
                }

                throw new SyntaxError('JSON.parse');
            }
        };
    }();
}

/**
 *
 *  UTF-8 data encode / decode
 *  http://www.webtoolkit.info/
 *  comments have been removed to skim the script. Ror doc visit the above url.
 **/

var Utf8 = {

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