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
package net.openkoncept.vroom.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class Event implements Serializable {

    protected String type;
    protected String method;
    protected Boolean sync;
    protected String beanClass;
    protected String var;
    protected String scope;

    protected List calls = new ArrayList();

    public Call addScriptCall(String script, String url) {
        Call call = new Call(script);
        call.setUrl(url);
        calls.add(call);
        return call;
    }

    public Call addUpdateCall(String id, String tag, String name, String attribute, String value, String url) {
        Call call = new Call(id, tag, name, attribute, value);
        call.setUrl(url);
        calls.add(call);
        return call;
    }

    public Event() {
    }

    public Event(String type, String method) {
        this(type, method, null, null, null, null);
    }

    public Event(String type, String method, Boolean sync) {
        this(type, method, sync, null, null, null);
    }

    public Event(String type, String method, Boolean sync, String beanClass, String var, String scope) {
        setType(type);
        setMethod(method);
        setSync(sync);
        setBeanClass(beanClass);
        setVar(var);
        setScope(scope);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean isSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        if (sync == null) {
            this.sync = Boolean.FALSE;
        } else {
            this.sync = sync;
        }
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        if (scope == null) {
            this.scope = Constants.SCOPE_SESSION;
        } else {
            this.scope = scope;
        }
    }

    public List getCalls() {
        return calls;
    }

}
